package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

/**
 * @author kuky.
 * @description
 */
class HomeArticleViewModel(private val repository: HomeArticleRepository) : ViewModel() {
    fun getHomeArticles() = repository.getHomeArticleStream().cachedIn(viewModelScope)
}