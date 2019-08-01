package com.kuky.demo.wan.android.ui.todoedit

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentTodoEditBinding
import com.kuky.demo.wan.android.entity.Todo

/**
 * @author kuky.
 * @description
 */
class TodoEditFragment : BaseFragment<FragmentTodoEditBinding>() {

    private var mTodo: Todo? = null

    override fun getLayoutId(): Int = R.layout.fragment_todo_edit

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            it.getSerializable("todo")?.let { p ->
                mTodo = p as Todo
            }
        }

        mBinding.title = if (mTodo == null) "新增待办" else "编辑待办"
        mBinding.todo = mTodo
    }

    companion object {
        fun addOrEditTodo(controller: NavController, @IdRes id: Int, todo: Todo?) =
            controller.navigate(id, Bundle().apply { putSerializable("todo", todo) })
    }
}