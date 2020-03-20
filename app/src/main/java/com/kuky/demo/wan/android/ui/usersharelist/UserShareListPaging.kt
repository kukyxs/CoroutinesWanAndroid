package com.kuky.demo.wan.android.ui.usersharelist

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.*
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */

class UserShareListRepository {

    suspend fun fetchUserShareList(page: Int): List<UserArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.userShareList(page, PreferencesHelper.fetchCookie(WanApplication.instance)).data.shareArticles.datas
    }

    suspend fun deleteShare(id: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.deleteAShare(id, PreferencesHelper.fetchCookie(WanApplication.instance))
    }

    suspend fun shareArticle(title: String, link: String) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.putAShare(title, link, PreferencesHelper.fetchCookie(WanApplication.instance))
    }
}

class UserShareDataSource(
    private val repository: UserShareListRepository
) : PageKeyedDataSource<Int, UserArticleDetail>(), CoroutineScope by IOScope() {
    val initState = MutableLiveData<NetworkState>()

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserArticleDetail>) {
        safeLaunch {
            block = {
                initState.postValue(NetworkState.LOADING)
                repository.fetchUserShareList(1)?.let {
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
                repository.fetchUserShareList(params.key)?.let {
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

class UserShareDataSourceFactory(
    private val repository: UserShareListRepository
) : DataSource.Factory<Int, UserArticleDetail>() {
    val sourceLiveData = MutableLiveData<UserShareDataSource>()

    override fun create(): DataSource<Int, UserArticleDetail> = UserShareDataSource(repository).apply {
        sourceLiveData.postValue(this)
    }
}