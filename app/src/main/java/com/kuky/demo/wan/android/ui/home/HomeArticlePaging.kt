package com.kuky.demo.wan.android.ui.home

import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.databinding.RecyclerHomeArticleBinding

/**
 * @author kuky.
 * @description
 */
class HomeArticlePagingSource(
    private val repository: HomeArticleRepository
) : PagingSource<Int, HomeArticleDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, HomeArticleDetail> {
        val page = params.key ?: 0
        return try {
            val article = if (page == 0) mutableListOf<HomeArticleDetail>().apply {
                addAll(repository.loadTops() ?: mutableListOf())
                addAll(repository.loadPageData(page) ?: mutableListOf())
            } else (repository.loadPageData(page) ?: mutableListOf())

            LoadResult.Page(
                data = article,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (article.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }
}

class HomeArticlePagingAdapter :
    BasePagingDataAdapter<HomeArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_home_article

    override fun setVariable(data: HomeArticleDetail, position: Int, holder: BaseViewHolder<RecyclerHomeArticleBinding>) {
        holder.binding.detail = data
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HomeArticleDetail>() {
            override fun areItemsTheSame(oldItem: HomeArticleDetail, newItem: HomeArticleDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: HomeArticleDetail, newItem: HomeArticleDetail): Boolean =
                oldItem == newItem
        }
    }
}