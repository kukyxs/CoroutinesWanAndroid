@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.hotproject

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class HotProjectViewModelFactory(
    private val repository: HotProjectRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HotProjectViewModel::class.java))
            return HotProjectViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}