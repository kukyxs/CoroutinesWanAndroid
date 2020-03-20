package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
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
 * Author: Taonce
 * Date: 2019/7/19
 * Project: CoroutinesWanAndroid
 * Desc:
 */
class CollectedArticlesRepository {
    private fun getCookie() = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun getCollectedArticleList(page: Int): List<UserCollectDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.userCollectedArticles(page, getCookie()).data.datas
    }

    suspend fun deleteCollectedArticle(articleId: Int, originId: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.unCollectCollection(articleId, originId, getCookie())
    }
}

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

class CollectedArticlesDataSourceFactory(
    private val repo: CollectedArticlesRepository
) : DataSource.Factory<Int, UserCollectDetail>() {
    val sourceLiveData = MutableLiveData<CollectedArticlesDataSources>()

    override fun create(): DataSource<Int, UserCollectDetail> = CollectedArticlesDataSources(repo).apply {
        sourceLiveData.postValue(this)
    }
}

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



