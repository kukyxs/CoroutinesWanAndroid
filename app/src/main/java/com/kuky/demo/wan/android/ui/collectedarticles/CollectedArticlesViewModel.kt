package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesViewModel(private val repository: CollectedArticlesRepository) : ViewModel() {

    fun getCollectedArticles() = repository.getCollectedArticlesStream().cachedIn(viewModelScope)

    fun removeCollectedArticle(articleId: Int, originId: Int) = repository.getRemoveResultStream(articleId, originId)
}

