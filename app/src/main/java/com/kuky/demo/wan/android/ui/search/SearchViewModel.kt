package com.kuky.demo.wan.android.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.CoroutineThrowableHandler
import com.kuky.demo.wan.android.base.PagingThrowableHandler
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.SearchHistoryUtils
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData

/**
 * @author kuky.
 * @description
 */
class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    val history = MutableLiveData<List<String>>()
    val hotKeys = MutableLiveData<List<HotKeyData>>()
    var result: LiveData<PagedList<ArticleDetail>>? = null

    fun fetchKeys(handler: CoroutineThrowableHandler) {
        viewModelScope.safeLaunch({
            hotKeys.value = repository.hotKeys()
            history.value = SearchHistoryUtils.fetchHistoryKeys(WanApplication.instance)
        }, { handler.invoke(it) })
    }

    fun fetchResult(key: String, handler: PagingThrowableHandler) {
        result = LivePagedListBuilder(
            SearchDataSourceFactory(repository, key, handler),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}