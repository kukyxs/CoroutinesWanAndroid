package com.kuky.demo.wan.android.ui.shareduser

import androidx.core.view.isInvisible
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.paging.PagingSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.databinding.RecyclerUserArticleBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import kotlinx.coroutines.CoroutineScope

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
        holder.binding.shareUser.isInvisible = true
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

//region adapter by paging2, has migrate to paging3
@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class UserSharedDataSource(
    private val repository: UserSharedRepository,
    private val userId: Int
) : PageKeyedDataSource<Int, UserArticleDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserArticleDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.fetchUserSharedArticles(userId, 1)?.let {
                    callback.onResult(it, null, 2)
                    initState.postValue(NetworkState.LOADED)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT))
            }
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserArticleDetail>) {
        safeLaunch {
            block = {
                repository.fetchUserSharedArticles(userId, params.key)?.let {
                    callback.onResult(it, params.key + 1)
                }
            }
            onError = {
                initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE))
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserArticleDetail>) {}
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class UserSharedDataSourceFactory(
    private val repository: UserSharedRepository,
    private val userId: Int
) : DataSource.Factory<Int, UserArticleDetail>() {
    val sourceLiveData = MutableLiveData<UserSharedDataSource>()

    override fun create(): DataSource<Int, UserArticleDetail> = UserSharedDataSource(repository, userId).apply {
        sourceLiveData.postValue(this)
    }
}

@Deprecated("migrate to paging3", level = DeprecationLevel.WARNING)
class UserSharedArticleAdapter : BasePagedListAdapter<UserArticleDetail, RecyclerUserArticleBinding>(DIFF_CALLBACK) {

    override fun getLayoutId(viewType: Int): Int = R.layout.recycler_user_article

    override fun setVariable(data: UserArticleDetail, position: Int, holder: BaseViewHolder<RecyclerUserArticleBinding>) {
        holder.binding.article = data
        holder.binding.shareUser.isInvisible = true
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
//endregion