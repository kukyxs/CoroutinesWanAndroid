package com.kuky.demo.wan.android.ui.todolist

import android.util.SparseIntArray
import androidx.core.content.ContextCompat
import androidx.core.util.forEach
import androidx.databinding.ViewDataBinding
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerParentTodoChoiceBinding
import com.kuky.demo.wan.android.databinding.RecyclerSubTodoChoiceBinding
import com.kuky.demo.wan.android.entity.Choice
import com.kuky.demo.wan.android.entity.ITodoChoice
import com.kuky.demo.wan.android.entity.TodoChoiceGroup
import com.kuky.demo.wan.android.utils.LogUtils

/**
 * @author kuky.
 * @description
 */

class TodoChoiceAdapter(choices: MutableList<TodoChoiceGroup>) : BaseRecyclerAdapter<ITodoChoice>(null) {
    companion object {
        const val PARENT_ITEM = 0xFF01
        const val CHILD_ITEM = 0xFF02
    }

    private val mIndexList = MutableList(choices.size) { 0 }
    private val mEndList = MutableList(choices.size) { 0 }
    private val mParamIndex = MutableList(choices.size) { "" }

    init {
        var totalEnd = -1
        var totalInitSelected = 0

        mData = arrayListOf<ITodoChoice>().apply {
            for (i in choices.indices) {
                val group = choices[i]

                this.add(group)
                this.addAll(group.choices)

                // 记录参数表
                mParamIndex[i] = group.param_key

                // 记录末尾表
                totalEnd += (1 + group.choices.size)
                mEndList[i] = totalEnd

                // 记录选中表
                totalInitSelected += 1
                mIndexList[i] = totalInitSelected
                totalInitSelected += group.choices.size
            }
        }
    }

    fun updateSelectedIndex(position: Int) {
        for (i in mEndList.indices) {
            if (mData!![position] is Choice && position <= mEndList[i]) {
                mIndexList[i] = position
                notifyDataSetChanged()
                break
            }
        }
    }

    fun getApiParams(): Map<String, Int> {
        val param = hashMapOf<String, Int>()

        for (i in mIndexList.indices) {
            val value = mData!![mIndexList[i]]

            if (value is Choice && value.type >= 0) {
                param[mParamIndex[i]] = value.type
            }
        }

        return param
    }

    override fun getLayoutId(viewType: Int): Int =
        if (viewType == PARENT_ITEM) R.layout.recycler_parent_todo_choice
        else R.layout.recycler_sub_todo_choice

    override fun setVariable(data: ITodoChoice, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        if (getItemViewType(position) == PARENT_ITEM) {
            (holder.binding as RecyclerParentTodoChoiceBinding).parentTodo = data as TodoChoiceGroup
        } else {
            (holder.binding as RecyclerSubTodoChoiceBinding).let {
                val context = holder.binding.root.context
                it.subTodo = data as Choice
                if (position in mIndexList) {
                    it.backgroundRes = ContextCompat.getDrawable(context, R.drawable.choice_selected)
                    it.colorRes = ContextCompat.getColor(context, R.color.colorPrimary)
                } else {
                    it.backgroundRes = ContextCompat.getDrawable(context, R.drawable.choice_unselected)
                    it.colorRes = ContextCompat.getColor(context, android.R.color.black)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int =
        if (mData!![position] is TodoChoiceGroup) PARENT_ITEM else CHILD_ITEM

}