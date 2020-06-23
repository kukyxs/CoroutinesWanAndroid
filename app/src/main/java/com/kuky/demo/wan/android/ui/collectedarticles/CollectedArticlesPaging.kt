package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerCollectedArticleBinding
import com.kuky.demo.wan.android.entity.UserCollectDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.withContext


/**
 * @author kuky.
 * @description
 */
class CollectedArticlesPagingSource(private val repository: CollectedArticlesRepository) :
    PagingSource<Int, UserCollectDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UserCollectDetail> {
        val page = params.key ?: 0

        return try {
            val collectedArticles = repository.getCollectedArticleList(page)

            LoadResult.Page(
                data = collectedArticles ?: mutableListOf(),
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (collectedArticles.isNullOrEmpty()) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}

class CollectedArticlesPagingAdapter : BasePagingDataAdapter<UserCollectDetail, RecyclerCollectedArticleBinding>(DIFF_CALLBACK) {
    override fun getLayoutId() = R.layout.recycler_collected_article

    override fun setVariable(
        data: UserCollectDetail, position: Int,
        holder: BaseViewHolder<RecyclerCollectedArticleBinding>
    ) {
        holder.binding.data = data
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

//region adapter by paging2, has migrate to paging3
@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CollectedArticlesDataSources(
    private val repo: CollectedArticlesRepository
) : PageKeyedDataSource<Int, UserCollectDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserCollectDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)

                repo.getCollectedArticleList(0)?.let {
                    callback.onResult(it, null, 1)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserCollectDetail>) {
        safeLaunch {
            block = {
                repo.getCollectedArticleList(params.key)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserCollectDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CollectedArticlesDataSourceFactory(
    private val repo: CollectedArticlesRepository
) : DataSource.Factory<Int, UserCollectDetail>() {
    val sourceLiveData = MutableLiveData<CollectedArticlesDataSources>()

    override fun create(): DataSource<Int, UserCollectDetail> = CollectedArticlesDataSources(repo).apply {
        sourceLiveData.postValue(this)
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class CollectedArticlesAdapter :
    BasePagedListAdapter<UserCollectDetail, RecyclerCollectedArticleBinding>(DIFF_CALLBACK) {
    override fun getLayoutId(viewType: Int) = R.layout.recycler_collected_article

    override fun setVariable(
        data: UserCollectDetail,
        position: Int,
        holder: BaseViewHolder<RecyclerCollectedArticleBinding>
    ) {
        holder.binding.data = data
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
//endregion