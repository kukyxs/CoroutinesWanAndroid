package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BasePagingDataAdapter
import com.kuky.demo.wan.android.base.BaseViewHolder
import com.kuky.demo.wan.android.extension.renderHtml
import com.kuky.demo.wan.android.databinding.RecyclerCollectedArticleBinding
import com.kuky.demo.wan.android.entity.UserCollectDetail


/**
 * @author kuky.
 * @description
 */
class CollectedArticlesPagingSource(
    private val repository: CollectedArticlesRepository
) : PagingSource<Int, UserCollectDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserCollectDetail> {
        val page = params.key ?: 0

        return try {
            val collectedArticles = repository.getCollectedArticleList(page)

            LoadResult.Page(
                data = collectedArticles,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (collectedArticles.isEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UserCollectDetail>) = state.anchorPosition
}

class CollectedArticlesPagingAdapter :
    BasePagingDataAdapter<UserCollectDetail, RecyclerCollectedArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId() = R.layout.recycler_collected_article

    override fun setVariable(
        data: UserCollectDetail, position: Int, holder: BaseViewHolder<RecyclerCollectedArticleBinding>
    ) {
        holder.binding.data = data
        holder.binding.description = data.desc.renderHtml()
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserCollectDetail>() {
            override fun areItemsTheSame(oldItem: UserCollectDetail, newItem: UserCollectDetail): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: UserCollectDetail, newItem: UserCollectDetail): Boolean =
                oldItem == newItem
        }
    }
}