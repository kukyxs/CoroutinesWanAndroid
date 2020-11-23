package com.kuky.demo.wan.android.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.fetchHistories
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class SearchViewModel(
    private val repository: SearchRepository
) : ViewModel() {

    val resultMode = MutableLiveData<Boolean>()

    private var mCurrentKey = ""
    private var mCurrentArticleResult: Flow<PagingData<ArticleDetail>>? = null

    init {
        resultMode.postValue(false)
    }

    fun getHotKeys() = flow {
        emit(repository.hotKeys())
    }

    fun fetchHistoryKeywords() = WanApplication.instance.fetchHistories()

    fun getSearchResult(key: String): Flow<PagingData<ArticleDetail>> {
        val lastResult = mCurrentArticleResult
        if (mCurrentKey == key && lastResult != null) return lastResult
        mCurrentKey = key

        return channelFlow {
            Pager(constPagerConfig) {
                SearchPagingSource(repository, key)
            }.flow.apply {
                mCurrentArticleResult = this
            }.cachedIn(viewModelScope).collectLatest { send(it) }
        }
    }
}