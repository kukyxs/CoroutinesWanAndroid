package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerCollectedWebsitesBinding
import com.kuky.demo.wan.android.entity.WebsiteData

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesAdapter : BaseRecyclerAdapter<WebsiteData>(null) {

    override fun setVariable(data: WebsiteData, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        (holder.binding as RecyclerCollectedWebsitesBinding).data = data
    }

    override fun getLayoutId(viewType: Int) = R.layout.recycler_collected_websites

    /**
     * 利用diffutil更新数据
     */
    fun update(newData: MutableList<WebsiteData>?) {
        val result = DiffUtil.calculateDiff(CollectedWebSiteDiffUtil(newData, getAdapterData()), true)
        mData = (mData ?: mutableListOf()).also {
            it.clear()
            it.addAll(newData ?: mutableListOf())
        }
        result.dispatchUpdatesTo(this)
    }
}

class CollectedWebSiteDiffUtil(
    private val newData: MutableList<WebsiteData>?,
    private val oldData: MutableList<WebsiteData>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
        if (newData.isNullOrEmpty() || oldData.isNullOrEmpty()) false
        else newData[newItemPosition].id == oldData[oldItemPosition].id

    override fun getOldListSize() = oldData?.size ?: 0

    override fun getNewListSize() = newData?.size ?: 0

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newData.isNullOrEmpty() || oldData.isNullOrEmpty()) false
        else newData[newItemPosition].name == oldData[oldItemPosition].name
                && newData[newItemPosition].link == oldData[oldItemPosition].link
}

