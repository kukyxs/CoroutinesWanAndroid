package com.kuky.demo.wan.android.ui.search

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData

/**
 * @author kuky.
 * @description
 */
class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    val resultMode = MutableLiveData<Boolean>()
    val keyNetState = MutableLiveData<NetworkState>()
    var netState: LiveData<NetworkState>? = null

    val history = MutableLiveData<List<String>>()
    val hotKeys = MutableLiveData<List<HotKeyData>>()
    var result: LiveData<PagedList<ArticleDetail>>? = null

    init {
        resultMode.postValue(false)
    }

    fun fetchKeys() {
        viewModelScope.safeLaunch {
            block = {
                keyNetState.postValue(NetworkState.LOADING)
                hotKeys.postValue(repository.hotKeys())
                updateHistory()
                keyNetState.postValue(NetworkState.LOADED)
            }
            onError = {
                keyNetState.postValue(NetworkState.error(it.message))
            }
        }
    }

    fun updateHistory() {
        history.postValue(SearchHistoryUtils.fetchHistoryKeys(WanApplication.instance))
    }

    fun fetchResult(key: String, empty: () -> Unit) {
        result = LivePagedListBuilder(
            SearchDataSourceFactory(repository, key).apply {
                netState = Transformations.switchMap(sourceLiveData) { it.initState }
            },
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<ArticleDetail>() {
            override fun onZeroItemsLoaded() = empty()
        }).build()
    }
}