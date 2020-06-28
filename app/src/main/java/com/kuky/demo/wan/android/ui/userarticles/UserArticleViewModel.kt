package com.kuky.demo.wan.android.ui.userarticles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.ui.app.constPagerConfig

/**
 * @author kuky.
 * @description
 */
class UserArticleViewModel(
    private val repository: UserArticleRepository
) : ViewModel() {

    fun getSharedArticles() = Pager(constPagerConfig) {
        UserArticlePagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}