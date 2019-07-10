package com.kuky.demo.wan.android.ui.search

import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.databinding.RecyclerHistoryBinding
import kotlinx.android.synthetic.main.recycler_history.view.*

/**
 * @author kuky.
 * @description
 */
class HistoryAdapter(list: MutableList<String>?) :
    BaseRecyclerAdapter<RecyclerHistoryBinding, String>(list) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_history

    override fun setVariable(data: String, position: Int, holder: BaseViewHolder<RecyclerHistoryBinding>) {
        holder.binding.history = data

        // TODO("解决点击问题不够优雅，待完善")
        holder.itemView.remove_history.setOnClickListener {
            SearchHistoryUtils.removeKeyword(holder.binding.root.context, data)
            mData?.remove(data)
            notifyItemRemoved(position)
        }
    }
}