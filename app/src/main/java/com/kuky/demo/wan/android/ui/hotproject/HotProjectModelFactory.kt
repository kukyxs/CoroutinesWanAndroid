package com.kuky.demo.wan.android.ui.hotproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class HotProjectModelFactory(private val repository: HotProjectRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HotProjectViewModel(repository) as T
    }
}