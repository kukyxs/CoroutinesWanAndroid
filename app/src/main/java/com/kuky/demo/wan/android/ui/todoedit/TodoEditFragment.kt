package com.kuky.demo.wan.android.ui.todoedit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentTodoEditBinding
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.utils.LogUtils
import kotlinx.android.synthetic.main.fragment_todo_edit.view.*
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import java.util.*

/**
 * @author kuky.
 * @description
 */
class TodoEditFragment : BaseFragment<FragmentTodoEditBinding>() {

    private val mViewModel: TodoEditViewModel by lazy {
        ViewModelProviders.of(requireActivity(), TodoEditViewModelFactory(TodoEditRepository()))
            .get(TodoEditViewModel::class.java)
    }

    private var mTodo: TodoInfo? = null

    override fun getLayoutId(): Int = R.layout.fragment_todo_edit

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            it.getSerializable("todo")?.let { p ->
                mTodo = p as TodoInfo
            }
        }

        mBinding.holder = this@TodoEditFragment
        mBinding.title = if (mTodo == null) "新增待办" else "编辑待办"
        mBinding.btnText = if (mTodo == null) "创建" else "修改"
        mBinding.todo = mTodo
    }

    @SuppressLint("SetTextI18n")
    fun datePick(view: View) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                LogUtils.error("$year-${month + 1}-$dayOfMonth")
                (view as TextView).text = "$year-${month + 1}-$dayOfMonth"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun typePick(view: View) {
        val types = mutableListOf("只用这一个", "工作", "学习", "生活")
        requireContext().selector("待办类别", types) { _, i ->
            (view as TextView).text = types[i]
        }
    }

    fun priorityPick(view: View) {
        val priorityList = mutableListOf("重要", "一般", "普通")
        val colors = mutableListOf(
            android.R.color.holo_red_dark,
            android.R.color.holo_orange_dark,
            android.R.color.holo_green_dark
        )

        requireContext().selector("待办级别", priorityList) { _, i ->
            (view as TextView).let {
                it.text = priorityList[i]
                it.setTextColor(ContextCompat.getColor(requireContext(), colors[i]))
            }
        }
    }

    fun updateOrAddTodo(view: View) {
        val title = mBinding.root.todo_title.text.toString()
        val content = mBinding.root.todo_description.text.toString()
        val date = mBinding.root.todo_date.text.toString()

        if (title.isBlank()) {
            requireContext().toast("标题不可为空")
            return
        }

        if (content.isBlank()) {
            requireContext().toast("详情不可为空")
            return
        }

        val param = hashMapOf<String, Any>()

        param["title"] = title
        param["content"] = content
        param["date"] = date
        param["type"] = 0
        param["priority"] = 0

        mViewModel.addTodo(param)
    }

    companion object {
        fun addOrEditTodo(controller: NavController, @IdRes id: Int, todo: TodoInfo?) =
            controller.navigate(id, Bundle().apply { putSerializable("todo", todo) })
    }
}