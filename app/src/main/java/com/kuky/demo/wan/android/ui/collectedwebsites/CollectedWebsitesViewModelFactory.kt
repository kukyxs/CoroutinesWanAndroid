@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesViewModelFactory(
    private val repository: CollectedWebsitesRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectedWebsitesViewModel::class.java))
            return CollectedWebsitesViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}

