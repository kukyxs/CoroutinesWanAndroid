package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.extension.renderHtml
import com.kuky.demo.wan.android.databinding.RecyclerWxChapterListBinding
import com.kuky.demo.wan.android.entity.WxChapterListDatas

/**
 * @author kuky.
 * @description
 */

class WxChapterListPagingSource(
    private val repository: WxChapterListRepository,
    private val wxId: Int, private val keyword: String
) : PagingSource<Int, WxChapterListDatas>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, WxChapterListDatas> {
        val page = params.key ?: 1

        return try {
            val chapters = repository.loadPage(wxId, page, keyword) ?: mutableListOf()
            LoadResult.Page(
                data = chapters,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (chapters.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, WxChapterListDatas>) = state.anchorPosition
}

class WxChapterPagingAdapter : BasePagingDataAdapter<WxChapterListDatas, RecyclerWxChapterListBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_wx_chapter_list

    override fun setVariable(
        data: WxChapterListDatas,
        position: Int,
        holder: BaseViewHolder<RecyclerWxChapterListBinding>
    ) {
        holder.binding.data = data
        holder.binding.title = data.title.renderHtml()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<WxChapterListDatas>() {
            override fun areItemsTheSame(oldItem: WxChapterListDatas, newItem: WxChapterListDatas): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WxChapterListDatas, newItem: WxChapterListDatas): Boolean =
                oldItem == newItem
        }
    }
}