package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesViewModel(
    private val repository: CollectedArticlesRepository
) : ViewModel() {

    fun getCollectedArticles() = Pager(constPagerConfig) {
        CollectedArticlesPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun removeCollectedArticle(articleId: Int, originId: Int) = flow {
        emit(repository.removeCollectedArticle(articleId, originId))
    }
}

