package com.kuky.demo.wan.android.ui.shareduser

import androidx.core.view.isInvisible
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import androidx.recyclerview.widget.DiffUtil
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.RecyclerUserArticleBinding
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */

class UserSharedRepository {
    suspend fun fetchUserSharedArticles(userId: Int, page: Int):
            List<UserArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.sharedUserInfo(userId, page, PreferencesHelper.fetchCookie(WanApplication.instance)).data.shareArticles.datas
    }

    suspend fun fetchUserCoinInfo(userId: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.sharedUserInfo(userId, 1, PreferencesHelper.fetchCookie(WanApplication.instance)).data
    }
}

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

class UserSharedDataSourceFactory(
    private val repository: UserSharedRepository,
    private val userId: Int
) : DataSource.Factory<Int, UserArticleDetail>() {
    val sourceLiveData = MutableLiveData<UserSharedDataSource>()

    override fun create(): DataSource<Int, UserArticleDetail> = UserSharedDataSource(repository, userId).apply {
        sourceLiveData.postValue(this)
    }
}

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