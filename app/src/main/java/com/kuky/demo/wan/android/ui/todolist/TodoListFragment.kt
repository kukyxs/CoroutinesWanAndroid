package com.kuky.demo.wan.android.ui.todolist

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentTodoListBinding
import com.kuky.demo.wan.android.utils.ScreenUtils
import kotlinx.android.synthetic.main.fragment_todo_list.view.*

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

    override fun getLayoutId(): Int = R.layout.fragment_todo_list

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        view.menu.let {
            it.layoutParams = it.layoutParams.apply {
                height = ScreenUtils.getScreenHeight(requireContext()) * 2 / 3
            }
        }
    }
}