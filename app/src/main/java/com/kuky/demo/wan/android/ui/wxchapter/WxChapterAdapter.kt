package com.kuky.demo.wan.android.ui.wxchapter

import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.databinding.RecyclerWxChapterBinding
import com.kuky.demo.wan.android.entity.WxChapterData

/**
 * @author Taonce.
 * @description
 */

class WxChapterAdapter(
    private val chapterList: MutableList<WxChapterData>
) : BaseRecyclerAdapter<RecyclerWxChapterBinding, WxChapterData>(chapterList) {
    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_wx_chapter

    override fun setVariable(
        data: WxChapterData,
        position: Int,
        holder: BaseViewHolder<RecyclerWxChapterBinding>
    ) {
        holder.binding.data = chapterList[position]
    }
}