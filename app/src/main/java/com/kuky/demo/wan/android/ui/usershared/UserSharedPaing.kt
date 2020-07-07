package com.kuky.demo.wan.android.ui.usershared

import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.base.renderHtml
import com.kuky.demo.wan.android.databinding.RecyclerUserArticleBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */
class UserSharedPagingSource(
    private val repository: UserSharedRepository, private val userId: Int
) : PagingSource<Int, UserArticleDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserArticleDetail> {
        val page = params.key ?: 1

        return try {
            val articles = repository.fetchUserSharedArticles(userId, page) ?: mutableListOf()
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

class UserSharedPagingAdapter : BasePagingDataAdapter<UserArticleDetail, RecyclerUserArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(): Int = R.layout.recycler_user_article

    override fun setVariable(data: UserArticleDetail, position: Int, holder: BaseViewHolder<RecyclerUserArticleBinding>) {
        holder.binding.article = data
        holder.binding.title = data.title.renderHtml()
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