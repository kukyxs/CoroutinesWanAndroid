@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class HomeArticleViewModelFactory(
    private val repository: HomeArticleRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeArticleViewModel::class.java))
            return HomeArticleViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}