package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.ui.app.constPagerConfig

/**
 * @author kuky.
 * @description
 */
class HomeArticleViewModel(
    private val repository: HomeArticleRepository
) : ViewModel() {

    fun getHomeArticles() = Pager(constPagerConfig) {
        HomeArticlePagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    @OptIn(ExperimentalPagingApi::class)
    fun getHomeArticlesByRoomCache() = Pager(
        constPagerConfig,
        remoteMediator = HomeArtRemoteMediator(repository, repository.db),
        pagingSourceFactory = repository.pagingSourceFactory
    ).flow.cachedIn(viewModelScope)
}