package com.kuky.demo.wan.android.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.RecyclerSearchArticleBinding
import com.kuky.demo.wan.android.entity.ArticleDetail
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

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
}

class SearchArticlePagingAdapter() : BasePagingDataAdapter<ArticleDetail, RecyclerSearchArticleBinding>(DIFF_CALLBACK) {
    override fun getLayoutId(): Int = R.layout.recycler_search_article

    override fun setVariable(data: ArticleDetail, position: Int, holder: BaseViewHolder<RecyclerSearchArticleBinding>) {
        holder.binding.detail = data
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

//region adapter by paging2, has migrate to paging3
@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class SearchDataSource(
    private val repository: SearchRepository, private val key: String
) : PageKeyedDataSource<Int, ArticleDetail>(), CoroutineScope by IOScope() {

    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.loadSearchResult(0, key)?.let {
                    callback.onResult(it, null, 1)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        safeLaunch {
            block = {
                repository.loadSearchResult(params.key, key)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class SearchDataSourceFactory(
    private val repository: SearchRepository, private val key: String
) : DataSource.Factory<Int, ArticleDetail>() {
    val sourceLiveData = MutableLiveData<SearchDataSource>()

    override fun create(): DataSource<Int, ArticleDetail> = SearchDataSource(repository, key).apply {
        sourceLiveData.postValue(this)
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class SearchArticleAdapter : BasePagedListAdapter<ArticleDetail, RecyclerSearchArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_search_article

    override fun setVariable(data: ArticleDetail, position: Int, holder: BaseViewHolder<RecyclerSearchArticleBinding>) {
        holder.binding.detail = data
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
//endregion