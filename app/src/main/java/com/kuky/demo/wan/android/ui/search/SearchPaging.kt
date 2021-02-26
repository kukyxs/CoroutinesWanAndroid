package com.kuky.demo.wan.android.ui.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.renderHtml
import com.kuky.demo.wan.android.databinding.RecyclerSearchArticleBinding
import com.kuky.demo.wan.android.entity.ArticleDetail

/**
 * @author kuky.
 * @description
 */
class SearchPagingSource(
    private val repository: SearchRepository, private val key: String
) : PagingSource<Int, ArticleDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleDetail> {
        val page = params.key ?: 0

        return try {
            val articles = repository.loadSearchResult(page, key) ?: mutableListOf()
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleDetail>) = state.anchorPosition
}

class SearchArticlePagingAdapter :
    BasePagingDataAdapter<ArticleDetail, RecyclerSearchArticleBinding>(DIFF_CALLBACK) {
    override fun getLayoutId(): Int = R.layout.recycler_search_article

    override fun setVariable(data: ArticleDetail, position: Int, holder: BaseViewHolder<RecyclerSearchArticleBinding>) {
        holder.binding.detail = data
        holder.binding.title = data.title.renderHtml()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleDetail>() {
            override fun areItemsTheSame(oldItem: ArticleDetail, newItem: ArticleDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ArticleDetail, newItem: ArticleDetail): Boolean =
                oldItem == newItem
        }
    }
}