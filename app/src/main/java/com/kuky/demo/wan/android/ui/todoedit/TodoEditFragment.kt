package com.kuky.demo.wan.android.ui.todoedit

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.hideSoftInput
import com.kuky.demo.wan.android.databinding.FragmentTodoEditBinding
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.ui.app.AppViewModel
import com.kuky.demo.wan.android.utils.TimeUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

/**
 * @author kuky.
 * @description
 */
class TodoEditFragment : BaseFragment<FragmentTodoEditBinding>() {

    private val mAppViewModel by activityViewModels<AppViewModel>()

    private val mViewModel by viewModel<TodoEditViewModel>()

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

        mBinding?.let { binding ->
            binding.holder = this@TodoEditFragment
            binding.todo = mTodo
            binding.title = if (mTodo == null) "新增待办" else "编辑待办"
            binding.btnText = if (mTodo == null) "创建" else "修改"
            binding.newDate = TimeUtils.formatDate(
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH) + 1,
                mCalendar.get(Calendar.DAY_OF_MONTH)
            )
            binding.todoTypeStr = if (mTodo == null) "工作"
            else when (mTodo?.type) {
                0 -> "只用这一个"
                1 -> "工作"
                2 -> "学习"
                3 -> "生活"
                else -> ""
            }
            binding.todoPriorityStr = if (mTodo == null) "重要"
            else when (mTodo?.priority) {
                1 -> "重要"
                2 -> "一般"
                3 -> "普通"
                else -> ""
            }
            binding.todoPriorityColor = ContextCompat.getColor(
                requireContext(), if (mTodo == null) android.R.color.holo_red_dark
                else when (mTodo?.priority) {
                    1 -> android.R.color.holo_red_dark
                    2 -> android.R.color.holo_orange_dark
                    3 -> android.R.color.holo_green_dark
                    else -> android.R.color.white
                }
            )
        }
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun updateOrAddTodo(view: View) {
        val title = mBinding?.todoTitle?.text.toString()
        val content = mBinding?.todoDescription?.text.toString()

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

        launch {
            if (mTodo == null) {
                mViewModel.addTodo(param).catch {
                    context?.toast(R.string.no_network)
                }.onStart {
                    mAppViewModel.showLoading()
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    mAppViewModel.needUpdateTodoList.postValue(true)
                    context?.toast(R.string.add_todo_succeed)
                    mNavController.navigateUp()
                }
            } else {
                mViewModel.updateTodo(mTodo?.id ?: 0, param).catch {
                    context?.toast(R.string.no_network)
                }.onStart {
                    mAppViewModel.showLoading()
                }.onCompletion {
                    mAppViewModel.dismissLoading()
                }.collectLatest {
                    mAppViewModel.needUpdateTodoList.postValue(true)
                    context?.toast(R.string.update_todo_succeed)
                    mNavController.navigateUp()
                }
            }
        }

        mBinding?.todoTitle?.hideSoftInput()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun deleteTodo(view: View) {
        launch {
            mViewModel.deleteTodo(mTodo?.id ?: 0).catch {
                context?.toast(R.string.no_network)
            }.onStart {
                mAppViewModel.showLoading()
            }.onCompletion {
                mAppViewModel.dismissLoading()
            }.collectLatest {
                mAppViewModel.needUpdateTodoList.postValue(true)
                context?.toast(R.string.delete_todo_succeed)
                mNavController.navigateUp()
            }
        }
    }

    companion object {
        fun addOrEditTodo(controller: NavController, @IdRes id: Int, todo: TodoInfo?) =
            controller.navigate(id, bundleOf("todo" to todo))
    }
}