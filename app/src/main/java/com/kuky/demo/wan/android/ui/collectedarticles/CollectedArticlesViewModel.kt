package com.kuky.demo.wan.android.ui.collectedarticles

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.base.BaseViewModel
import com.kuky.demo.wan.android.base.UiState
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesViewModel(
    application: Application,
    private val repository: CollectedArticlesRepository
) : BaseViewModel(application) {

    private val _removeState = MutableStateFlow<UiState>(UiState.Create)
    val removeState: StateFlow<UiState> = _removeState

    suspend fun getCollectedArticles() = Pager(constPagerConfig) {
        CollectedArticlesPagingSource(repository)
    }.flow.cachedIn(viewModelScope).doRequest()

    suspend fun removeCollectedArticle(articleId: Int, originId: Int) = flow {
        emit(repository.removeCollectedArticle(articleId, originId))
    }.doRequest(_removeState)
}

