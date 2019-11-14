package com.kuky.demo.wan.android.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.ERROR_CODE_INIT
import com.kuky.demo.wan.android.base.ERROR_CODE_MORE
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description
 */

class SearchRepository {

    // 搜索热词
    suspend fun hotKeys() = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.hotKeys().data
    }

    // 搜索结果
    suspend fun loadSearchResult(page: Int, key: String): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.searchArticle(page, key, PreferencesHelper.fetchCookie(WanApplication.instance)).data.datas
    }
}

class SearchDataSource(
    private val repository: SearchRepository, private val key: String
) : PageKeyedDataSource<Int, ArticleDetail>(), CoroutineScope by MainScope() {

    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) {
        safeLaunch({
            initState.postValue(NetworkState.LOADING)
            repository.loadSearchResult(0, key)?.let {
                callback.onResult(it, null, 1)
                initState.postValue(NetworkState.LOADED)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_INIT)) })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        safeLaunch({
            repository.loadSearchResult(params.key, key)?.let {
                callback.onResult(it, params.key + 1)
            }
        }, { initState.postValue(NetworkState.error(it.message, ERROR_CODE_MORE)) })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {}

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class SearchDataSourceFactory(
    private val repository: SearchRepository, private val key: String
) : DataSource.Factory<Int, ArticleDetail>() {
    val sourceLiveData = MutableLiveData<SearchDataSource>()

    override fun create(): DataSource<Int, ArticleDetail> = SearchDataSource(repository, key).apply {
        sourceLiveData.postValue(this)
    }
}