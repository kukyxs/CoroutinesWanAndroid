package com.kuky.demo.wan.android.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


/**
 * Author: Taonce
 * Date: 2019/8/1
 * Desc:
 */

class CollectionModelFactory(private val repo: CollectionRepository) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CollectionViewModel(repo) as T
    }
}