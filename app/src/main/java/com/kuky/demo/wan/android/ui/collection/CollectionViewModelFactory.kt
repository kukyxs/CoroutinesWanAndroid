@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * @author kuky.
 * @description
 */
class CollectionViewModelFactory(
    private val repository: CollectionRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CollectionViewModel::class.java))
            return CollectionViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}