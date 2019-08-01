package com.kuky.demo.wan.android.ui.todolist

import android.os.Bundle
import android.view.View
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
import com.kuky.demo.wan.android.ui.todoedit.TodoEditFragment
import com.kuky.demo.wan.android.utils.AssetsLoader
import com.kuky.demo.wan.android.utils.LogUtils

/**
 * @author kuky.
 * @description 展示待办列表，筛选项：
 * ```
 *      {
 *      status 状态: [0-未完成, 1-完成, 默认全部],
 *      type 创建类型: [1-工作, 2-生活, 3-娱乐, 默认全部],
 *      priority 优先级: [1-High, 2-Normal, 3-Low, 默认全部],
 *      orderby 排序: [1-完成日期顺序, 2-完成日期逆序, 3-创建日期顺序, 4-创建日期逆序(默认)]
 *      }
 * ```
 */
class TodoListFragment : BaseFragment<FragmentTodoListBinding>() {

    private val mChoiceAdapter: TodoChoiceAdapter by lazy {
        TodoChoiceAdapter(
            Gson().fromJson(
                AssetsLoader.getTextFromAssets(requireContext(), "todo_choices.json"),
                object : TypeToken<List<TodoChoiceGroup>>() {}.type
            ) ?: arrayListOf()
        )
    }

    private val mChoiceLayoutManager: FlexboxLayoutManager by lazy {
        FlexboxLayoutManager(requireContext(), FlexDirection.ROW, FlexWrap.WRAP)
    }

    override fun getLayoutId(): Int = R.layout.fragment_todo_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@TodoListFragment

        mBinding.todoItemClick = OnItemClickListener { position, v -> }
        mBinding.todoItemLongClick = OnItemLongClickListener { position, v -> true }

        mBinding.choiceAdapter = mChoiceAdapter
        mBinding.choiceLayoutManager = mChoiceLayoutManager
        mBinding.choiceItemClick = OnItemClickListener { position, _ ->
            mChoiceAdapter.getItemData(position)?.let {
                if (it is Choice) {
                    mChoiceAdapter.updateSelectedIndex(position)
                    LogUtils.error(mChoiceAdapter.getApiParams())
                }
            }
        }
    }

    fun addTodo(view: View) {
        TodoEditFragment.addOrEditTodo(mNavController, R.id.action_todoListFragment_to_todoEditFragment, null)
    }
}