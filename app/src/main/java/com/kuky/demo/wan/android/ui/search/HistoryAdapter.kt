package com.kuky.demo.wan.android.ui.search

import android.text.TextUtils
import android.view.View
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
class HistoryAdapter(list: MutableList<String>? = null) :
    BaseRecyclerAdapter<RecyclerHistoryBinding, String>(list) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_history

    override fun setVariable(data: String, position: Int, holder: BaseViewHolder<RecyclerHistoryBinding>) {
        holder.binding.history = data
        holder.binding.listener = View.OnClickListener {
            SearchHistoryUtils.removeKeyword(holder.binding.root.context, data)
            mData?.remove(data)
            notifyItemRemoved(position)
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