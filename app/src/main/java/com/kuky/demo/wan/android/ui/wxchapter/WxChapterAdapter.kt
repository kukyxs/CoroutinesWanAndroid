package com.kuky.demo.wan.android.ui.wxchapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerWxChapterBinding
import com.kuky.demo.wan.android.entity.WxChapterData

/**
 * @author kuky.
 * @description
 */
class WxChapterAdapter(chapterList: MutableList<WxChapterData>? = mutableListOf()) :
    BaseRecyclerAdapter<WxChapterData>(chapterList) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_wx_chapter

    override fun setVariable(data: WxChapterData, position: Int, holder: BaseViewHolder<ViewDataBinding>) {
        (holder.binding as RecyclerWxChapterBinding).data = data
    }

    fun update(data: MutableList<WxChapterData>?) {
        val diffResult = DiffUtil.calculateDiff(ChapterDiffUtil(data, getAdapterData()), true)
        diffResult.dispatchUpdatesTo(this)
        mData = (mData ?: mutableListOf()).apply {
            clear()
            addAll(data ?: mutableListOf())
        }
    }
}

class ChapterDiffUtil(
    private val newData: MutableList<WxChapterData>?,
    private val oldData: MutableList<WxChapterData>?
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newData.isNullOrEmpty() || oldData.isNullOrEmpty()) false
        else newData[newItemPosition].id == oldData[oldItemPosition].id

    override fun getOldListSize(): Int = oldData?.size ?: 0

    override fun getNewListSize(): Int = newData?.size ?: 0


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        if (newData.isNullOrEmpty() || oldData.isNullOrEmpty()) false
        else newData[newItemPosition].name == oldData[oldItemPosition].name
}
