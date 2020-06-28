@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * @author kuky.
 * @description
 */
class CollectedArticlesViewModelFactory(
    private val repository: CollectedArticlesRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectedArticlesViewModel::class.java))
            return CollectedArticlesViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}

