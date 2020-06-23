package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig

/**
 * @author kuky.
 * @description
 */
class HomeArticleViewModel(private val repository: HomeArticleRepository) : ViewModel() {
    val homeArticleList = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = true, prefetchDistance = 5)
    ) { HomeArticlePagingSource(repository) }.flow
}