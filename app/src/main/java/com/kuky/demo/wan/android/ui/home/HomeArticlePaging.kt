package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.WanDatabaseUtils
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.databinding.RecyclerHomeArticleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class HomeArticlePagingSource(private val repository: HomeArticleRepository) :
    PagingSource<Int, HomeArticleDetail>() {

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

class HomeArticlePagingAdapter : BasePagingDataAdapter<HomeArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {
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

//region adapter by paging2, has migrate to paging3
@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class HomeArticleDataSource(
    private val repository: HomeArticleRepository
) : PageKeyedDataSource<Int, HomeArticleDetail>(), CoroutineScope by IOScope() {

    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, HomeArticleDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                val tops = repository.loadTops()
                val data = repository.loadPageData(0)

                callback.onResult(arrayListOf<HomeArticleDetail>().apply {
                    addAll(tops ?: arrayListOf())
                    addAll(data ?: arrayListOf())
                }, null, 1)

                initState.postValue(NetworkState.LOADED)

                withContext(Dispatchers.IO) {
                    WanDatabaseUtils.homeArticleCacheDao.clearHomeCache()
                    tops?.let { WanDatabaseUtils.homeArticleCacheDao.cacheHomeArticles(it) }
                    data?.let { WanDatabaseUtils.homeArticleCacheDao.cacheHomeArticles(it) }
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, HomeArticleDetail>) {
        safeLaunch {
            block = {
                repository.loadPageData(params.key)?.let {
                    withContext(Dispatchers.IO) {
                        WanDatabaseUtils.homeArticleCacheDao.cacheHomeArticles(it)
                    }

                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, HomeArticleDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class HomeArticleDataSourceFactory(
    private val repository: HomeArticleRepository
) : DataSource.Factory<Int, HomeArticleDetail>() {
    val sourceLiveData = MutableLiveData<HomeArticleDataSource>()

    override fun create(): DataSource<Int, HomeArticleDetail> = HomeArticleDataSource(repository).apply {
        sourceLiveData.postValue(this)
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class HomeArticleAdapter : BasePagedListAdapter<HomeArticleDetail, RecyclerHomeArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_home_article

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
//endregion
