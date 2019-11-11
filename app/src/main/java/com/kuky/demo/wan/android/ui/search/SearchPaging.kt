package com.kuky.demo.wan.android.ui.search

import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
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
        RetrofitManager.apiService.hotKeys()
    }

    // 搜索结果
    suspend fun loadSearchResult(page: Int, key: String): List<ArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.searchArticle(page, key, PreferencesHelper.fetchCookie(WanApplication.instance)).data.datas
    }
}

class SearchDataSource(
    private val repository: SearchRepository, private val key: String,
    private val handler: PagingThrowableHandler
) : PageKeyedDataSource<Int, ArticleDetail>(), CoroutineScope by MainScope() {

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ArticleDetail>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_INITIAL, it)
        }, {
            val data = repository.loadSearchResult(0, key)
            data?.let {
                callback.onResult(it, null, 1)
            }
        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_AFTER, it)
        }, {
            val data = repository.loadSearchResult(params.key, key)
            data?.let {
                callback.onResult(it, params.key + 1)
            }
        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ArticleDetail>) {
        safeLaunch({
            handler.invoke(PAGING_THROWABLE_LOAD_CODE_BEFORE, it)
        }, {
            val data = repository.loadSearchResult(params.key, key)
            data?.let {
                callback.onResult(it, params.key - 1)
            }
        })
    }

    override fun invalidate() {
        super.invalidate()
        cancel()
    }
}

class SearchDataSourceFactory(
    private val repository: SearchRepository, private val key: String,
    private val handler: PagingThrowableHandler
) : DataSource.Factory<Int, ArticleDetail>() {
    override fun create(): DataSource<Int, ArticleDetail> = SearchDataSource(repository, key, handler)
}