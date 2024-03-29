package com.kuky.demo.wan.android.ui.userarticles

import android.graphics.Paint
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.extension.renderHtml
import com.kuky.demo.wan.android.databinding.RecyclerUserArticleBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */

class UserArticlePagingSource(
    private val repository: UserArticleRepository
) : PagingSource<Int, UserArticleDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserArticleDetail> {
        val page = params.key ?: 0

        return try {
            val articles = repository.fetchUserArticles(page) ?: mutableListOf()

            LoadResult.Page(
                data = articles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserArticleDetail>) = state.anchorPosition
}

class UserArticlePagingAdapter : BasePagingDataAdapter<UserArticleDetail, RecyclerUserArticleBinding>(DIFF_CALLBACK) {

    var userListener: ((Int, String) -> Unit)? = null

    override fun getLayoutId(): Int = R.layout.recycler_user_article

    override fun setVariable(data: UserArticleDetail, position: Int, holder: BaseViewHolder<RecyclerUserArticleBinding>) {
        holder.binding.article = data
        holder.binding.title = data.title.renderHtml()
        holder.binding.shareUser.let {
            it.paint.flags = it.paint.flags or Paint.UNDERLINE_TEXT_FLAG
            it.setOnClickListener {
                userListener?.invoke(data.userId, data.shareUser)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserArticleDetail>() {
            override fun areItemsTheSame(oldItem: UserArticleDetail, newItem: UserArticleDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserArticleDetail, newItem: UserArticleDetail): Boolean =
                oldItem == newItem
        }
    }
}