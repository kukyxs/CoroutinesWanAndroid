package com.kuky.demo.wan.android.ui.todoedit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentTodoEditBinding
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.ui.todolist.UpdateListViewModel
import com.kuky.demo.wan.android.utils.LogUtils
import com.kuky.demo.wan.android.utils.TimeUtils
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
        ViewModelProvider(requireActivity(), TodoEditViewModelFactory(TodoEditRepository()))
            .get(TodoEditViewModel::class.java)
    }

    private val mUpdateListFlag: UpdateListViewModel by lazy {
        getSharedViewModel(UpdateListViewModel::class.java)
    }

    private val mCalendar: Calendar by lazy {
        Calendar.getInstance()
    }

    private var mTodo: TodoInfo? = null

    override fun getLayoutId(): Int = R.layout.fragment_todo_edit

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            it.getSerializable("todo")?.let { p ->
                mTodo = (p as TodoInfo).apply {
                    mViewModel.todoType.value = this.type
                    mViewModel.todoPriority.value = this.priority
                    mViewModel.todoDate.value = this.dateStr
                    mViewModel.todoState.value = this.status
                }
            }
        }

        mBinding.holder = this@TodoEditFragment
        mBinding.todo = mTodo
        mBinding.title = if (mTodo == null) "新增待办" else "编辑待办"
        mBinding.btnText = if (mTodo == null) "创建" else "修改"
        mBinding.newDate = TimeUtils.formatDate(
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH) + 1,
            mCalendar.get(Calendar.DAY_OF_MONTH)
        )
        mBinding.todoTypeStr = if (mTodo == null) "工作"
        else when (mTodo?.type) {
            0 -> "只用这一个"
            1 -> "工作"
            2 -> "学习"
            3 -> "生活"
            else -> ""
        }
        mBinding.todoPriorityStr = if (mTodo == null) "重要"
        else when (mTodo?.priority) {
            1 -> "重要"
            2 -> "一般"
            3 -> "普通"
            else -> ""
        }
        mBinding.todoPriorityColor = ContextCompat.getColor(
            requireContext(), if (mTodo == null) android.R.color.holo_red_dark
            else when (mTodo?.priority) {
                1 -> android.R.color.holo_red_dark
                2 -> android.R.color.holo_orange_dark
                3 -> android.R.color.holo_green_dark
                else -> android.R.color.white
            }
        )
    }

    @SuppressLint("SetTextI18n")
    fun datePick(view: View) {
        DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                TimeUtils.formatDate(year, month + 1, dayOfMonth).let {
                    (view as TextView).text = it
                    mViewModel.todoDate.value = it
                }
            },
            mCalendar.get(Calendar.YEAR),
            mCalendar.get(Calendar.MONTH),
            mCalendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun typePick(view: View) {
        val types = mutableListOf("工作", "学习", "生活")
        requireContext().selector("待办类别", types) { _, i ->
            (view as TextView).text = types[i]
            mViewModel.todoType.value = i + 1
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
                mViewModel.todoPriority.value = i + 1
            }
        }
    }

    fun updateOrAddTodo(view: View) {
        val title = mBinding.root.todo_title.text.toString()
        val content = mBinding.root.todo_description.text.toString()

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
        param["date"] = mViewModel.todoDate.value ?: ""
        param["type"] = mViewModel.todoType.value ?: 1
        param["priority"] = mViewModel.todoPriority.value ?: 1
        param["status"] = mViewModel.todoState.value ?: 0

        LogUtils.error(param)

        if (mTodo == null) {
            mViewModel.addTodo(param, {
                mUpdateListFlag.needUpdate.value = true
                requireContext().toast("添加待办成功")
                mNavController.navigateUp()
            }, { message -> requireContext().toast(message) })
        } else {
            mViewModel.updateTodo(mTodo?.id ?: 0, param, {
                mUpdateListFlag.needUpdate.value = true
                requireContext().toast("更新待办成功")
                mNavController.navigateUp()
            }, { message -> requireContext().toast(message) })
        }
    }

    fun deleteTodo(view: View) {
        mViewModel.deleteTodo(mTodo?.id ?: 0, {
            mUpdateListFlag.needUpdate.value = true
            requireContext().toast("删除成功")
            mNavController.navigateUp()
        }, { message ->
            requireContext().toast(message)
        })
    }

    companion object {
        fun addOrEditTodo(controller: NavController, @IdRes id: Int, todo: TodoInfo?) =
            controller.navigate(id, Bundle().apply { putSerializable("todo", todo) })
    }
}