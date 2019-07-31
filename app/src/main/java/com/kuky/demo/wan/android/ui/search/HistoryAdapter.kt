package com.kuky.demo.wan.android.ui.search

import android.text.TextUtils
import android.view.View
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.databinding.RecyclerHistoryBinding

/**
 * @author kuky.
 * @description
 */
class HistoryAdapter(list: MutableList<String>? = null) : BaseRecyclerAdapter<String>(list) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_history

    override fun setVariable(data: String, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        (holder.binding as RecyclerHistoryBinding).let {
            it.history = data
            it.listener = View.OnClickListener {
                SearchHistoryUtils.removeKeyword(holder.binding.root.context, data)
                mData?.remove(data)
                // TODO("notifyItemRemoved 位置计算有误，先使用 notifyDataSetChanged 代替")
                notifyDataSetChanged()
            }
        }
    }

    fun updateHistory(history: MutableList<String>) {
        val result = DiffUtil.calculateDiff(HistoryDiffCall(history, getAdapterData()), true)
        result.dispatchUpdatesTo(this)
        mData = (mData ?: arrayListOf()).apply {
            clear()
            addAll(history)
        }
    }
}

class HistoryDiffCall(private val newList: MutableList<String>?, private val oldList: MutableList<String>?) :
    DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newList.isNullOrEmpty() || oldList.isNullOrEmpty()) false
        else TextUtils.equals(newList[newItemPosition], oldList[oldItemPosition])

    override fun getOldListSize(): Int = oldList?.size ?: 0

    override fun getNewListSize(): Int = newList?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newList.isNullOrEmpty() || oldList.isNullOrEmpty()) false
        else TextUtils.equals(newList[newItemPosition], oldList[oldItemPosition])
}