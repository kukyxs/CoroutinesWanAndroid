package com.kuky.demo.wan.android.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.entity.HotKeyData
import kotlinx.coroutines.launch

/**
 * @author kuky.
 * @description
 */
class SearchViewModel(private val repository: SearchRepository) : ViewModel() {

    val hotKeys = MutableLiveData<List<HotKeyData>>()
    var result: LiveData<PagedList<ArticleDetail>>? = null

    fun fetchKeys() {
        viewModelScope.launch {
            hotKeys.value = repository.hotKeys().data
        }
    }

    fun fetchResult(key: String) {
        result = LivePagedListBuilder(
            SearchDataSourceFactory(repository, key),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}