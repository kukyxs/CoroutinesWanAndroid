package com.kuky.demo.wan.android.ui.todolist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.FragmentTodoListBinding
import com.kuky.demo.wan.android.entity.Choice
import com.kuky.demo.wan.android.entity.TodoChoiceGroup
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.ui.app.PagingLoadStateAdapter
import com.kuky.demo.wan.android.ui.todoedit.TodoEditFragment
import com.kuky.demo.wan.android.utils.loadTextFromAssets
import com.kuky.demo.wan.android.widget.ErrorReload
import com.kuky.demo.wan.android.widget.RequestStatusCode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import org.koin.androidx.scope.lifecycleScope
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * @author kuky.
 * @description 展示待办列表，筛选项：
 * ```
 *      {
 *      status 状态: [0-未完成, 1-完成, 默认全部],
 *      type 创建类型: [1-只用这一个, 2-工作, 3-学习, 4-生活, -1-默认全部],
 *      priority 优先级: [1-重要, 2-一般, 3-普通, -1-默认全部],
 *      orderby 排序: [1-完成日期顺序, 2-完成日期逆序, 3-创建日期顺序, 4-创建日期逆序(默认)]
 *      }
 * ```
 * 查询参数通过 [TodoChoiceAdapter] #getApiParam 获取即可
 */
class TodoListFragment : BaseFragment<FragmentTodoListBinding>() {

    private val mAppViewMode by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<TodoListViewModel>()

    private val mChoiceAdapter by lifecycleScope.inject<TodoChoiceAdapter> {
        parametersOf(
            Gson().fromJson(
                context?.loadTextFromAssets("todo_choices.json"),
                object : TypeToken<MutableList<TodoChoiceGroup>>() {}.type
            )
        )
    }

    private val mTodoAdapter by lifecycleScope.inject<TodoListPagingAdapter>()

    private val mChoiceLayoutManager by lazy {
        FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
    }

    private val mTodoLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private var mParams: HashMap<String, Int>? = null

    private var mTodoJob: Job? = null

    override fun actionsOnViewInflate() {
        fetchTodoList()

        mAppViewMode.needUpdateTodoList.observe(this, Observer {
            if (it) fetchTodoList(true)
        })
    }

    override fun getLayoutId(): Int = R.layout.fragment_todo_list

    @Suppress("LABEL_NAME_CLASH")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.run {
            holder = this@TodoListFragment

            refreshColor = R.color.colorAccent
            refreshListener = SwipeRefreshLayout.OnRefreshListener {
                mTodoAdapter.refresh()
            }

            todoAdapter = mTodoAdapter.apply {
                addLoadStateListener { loadState ->
                    refreshing = loadState.refresh is LoadState.Loading
                    statusCode = when (loadState.refresh) {
                        is LoadState.Loading -> RequestStatusCode.Loading
                        is LoadState.Error -> RequestStatusCode.Error
                        else -> {
                            if (itemCount == 0) RequestStatusCode.Empty
                            else RequestStatusCode.Succeed
                        }
                    }
                }
            }.withLoadStateFooter(
                PagingLoadStateAdapter { mTodoAdapter.retry() }
            )

            todoLayoutManager = mTodoLayoutManager

            todoItemClick = OnItemClickListener { position, _ ->
                mTodoAdapter.getItemData(position)?.let {
                    if (it.status == 1) {
                        requireContext().alert("当前 Todo 已完成，无法更新内容，请长按修改 Todo 状态后再进行更新") {
                            yesButton { }
                        }.show()
                        return@let
                    }
                    TodoEditFragment.addOrEditTodo(findNavController(), R.id.action_todoListFragment_to_todoEditFragment, it)
                }
            }

            todoItemLongClick = OnItemLongClickListener { position, _ ->
                mTodoAdapter.getItemData(position)?.let { todo ->
                    requireContext().alert("是否设置当前待办完成状态为${if (todo.status == 0) "完成" else "未完成"}") {
                        yesButton { changeTodoState(todo) }
                        noButton { }
                    }.show()
                }
            }
            scrollListener = object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val firstCompletedVisibleItems = IntArray(2)
                    mTodoLayoutManager.findFirstCompletelyVisibleItemPositions(firstCompletedVisibleItems)
                    enabled = firstCompletedVisibleItems.contains(0)
                }
            }

            choiceAdapter = mChoiceAdapter
            choiceLayoutManager = mChoiceLayoutManager
            choiceItemClick = OnItemClickListener { position, _ ->
                mChoiceAdapter.getItemData(position)?.let {
                    if (it is Choice) {
                        mChoiceAdapter.updateSelectedIndex(position)
                        fetchTodoList()
                    }
                }
            }

            gesture = DoubleClickListener {
                doubleTap = { todoListPage.scrollToTop() }
            }

            errorReload = ErrorReload { fetchTodoList(true) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun changeTodoState(todo: TodoInfo) {
        launch {
            mViewModel.updateTodoState(todo.id, if (todo.status == 0) 1 else 0).catch {
                context?.toast(R.string.no_network)
            }.onStart {
                mAppViewMode.showLoading()
            }.onCompletion {
                mAppViewMode.dismissLoading()
            }.collectLatest {
                context?.toast(R.string.change_todo_state)
                mAppViewMode.needUpdateTodoList.postValue(true)
            }
        }
    }

    private fun fetchTodoList(isRefresh: Boolean = false) {
        val param = mChoiceAdapter.getApiParams()

        if (param == mParams && !isRefresh) return

        mParams = param

        mTodoJob?.cancel()
        mTodoJob = launch {
            mViewModel.getTodoList(param)
                .catch { mBinding?.statusCode = RequestStatusCode.Error }
                .collectLatest { mTodoAdapter.submitData(it) }
        }
    }

    fun addTodo(view: View) {
        mBinding?.settingDrawer?.animClosed()
        TodoEditFragment.addOrEditTodo(findNavController(), R.id.action_todoListFragment_to_todoEditFragment, null)
    }
}