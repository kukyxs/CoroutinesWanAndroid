package com.kuky.demo.wan.android.ui.todolist

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.databinding.FragmentTodoListBinding
import com.kuky.demo.wan.android.entity.Choice
import com.kuky.demo.wan.android.entity.TodoChoiceGroup
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.ui.todoedit.TodoEditFragment
import com.kuky.demo.wan.android.utils.AssetsLoader
import kotlinx.android.synthetic.main.fragment_todo_list.view.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton

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
    companion object {
        private val mHandler = Handler()
    }

    private val mViewModel: TodoListViewModel by lazy {
        ViewModelProvider(requireActivity(), TodoListViewModelFactory(TodoRepository()))
            .get(TodoListViewModel::class.java)
    }

    private val mUpdateFlag: UpdateListViewModel by lazy {
        getSharedViewModel(UpdateListViewModel::class.java)
    }

    private val mChoiceAdapter: TodoChoiceAdapter by lazy {
        TodoChoiceAdapter(
            Gson().fromJson(
                AssetsLoader.getTextFromAssets(requireContext(), "todo_choices.json"),
                object : TypeToken<List<TodoChoiceGroup>>() {}.type
            ) ?: arrayListOf()
        )
    }

    private val mTodoAdapter: TodoPagingAdapter by lazy {
        TodoPagingAdapter()
    }

    private val mChoiceLayoutManager: FlexboxLayoutManager by lazy {
        FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
    }

    private val mTodoLayoutManager: StaggeredGridLayoutManager by lazy {
        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private var mParams: HashMap<String, Int>? = null

    override fun getLayoutId(): Int = R.layout.fragment_todo_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@TodoListFragment

        mBinding.refreshColor = R.color.colorAccent
        mBinding.refreshListener = SwipeRefreshLayout.OnRefreshListener {
            fetchTodoList(true)
        }

        mBinding.todoAdapter = mTodoAdapter
        mBinding.todoLayoutManager = mTodoLayoutManager
        mBinding.todoItemClick = OnItemClickListener { position, v ->
            mTodoAdapter.getItemData(position)?.let {
                if (it.status == 1) {
                    requireContext().alert("当前 Todo 已完成，无法更新内容，请长按修改 Todo 状态后再进行更新") {
                        yesButton { }
                    }.show()
                    return@let
                }
                TodoEditFragment.addOrEditTodo(mNavController, R.id.action_todoListFragment_to_todoEditFragment, it)
            }
        }
        mBinding.todoItemLongClick = OnItemLongClickListener { position, _ ->
            mTodoAdapter.getItemData(position)?.let { todo ->
                requireContext().alert("是否设置当前待办完成状态为${if (todo.status == 0) "完成" else "未完成"}") {
                    yesButton {
                        mViewModel.updateTodoState(todo.id, if (todo.status == 0) 1 else 0, {
                            requireContext().toast("修改成功")
                            mUpdateFlag.needUpdate.value = true
                        }, { message -> requireContext().toast(message) })
                    }
                    noButton { }
                }.show()
            }
            true
        }
        mBinding.scrollListener = object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val firstCompletedVisibleItems = IntArray(2)
                mTodoLayoutManager.findFirstCompletelyVisibleItemPositions(firstCompletedVisibleItems)
                mBinding.enabled = firstCompletedVisibleItems.contains(0)
            }
        }

        mBinding.choiceAdapter = mChoiceAdapter
        mBinding.choiceLayoutManager = mChoiceLayoutManager
        mBinding.choiceItemClick = OnItemClickListener { position, _ ->
            mChoiceAdapter.getItemData(position)?.let {
                if (it is Choice) {
                    mChoiceAdapter.updateSelectedIndex(position)
                    fetchTodoList()
                }
            }
        }

        mUpdateFlag.needUpdate.observe(this, Observer<Boolean> {
            if (it) fetchTodoList(true)
        })

        fetchTodoList()
    }

    private fun fetchTodoList(isRefresh: Boolean = false) {
        val param = mChoiceAdapter.getApiParams()

        if (param == mParams && !isRefresh) return

        mParams = param
        mViewModel.fetchTodoList(param)
        mBinding.refreshing = true
        mViewModel.todoList?.observe(this, Observer<PagedList<TodoInfo>> {
            mTodoAdapter.submitList(it)
            mHandler.postDelayed({ mBinding.refreshing = false }, 500)
        })
    }

    fun addTodo(view: View) {
        mBinding.root.setting_drawer.animClosed()
        TodoEditFragment.addOrEditTodo(mNavController, R.id.action_todoListFragment_to_todoEditFragment, null)
    }
}