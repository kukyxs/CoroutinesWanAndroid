package com.kuky.demo.wan.android.ui.todoedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class TodoEditViewModelFactory(private val repository: TodoEditRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TodoEditViewModel(repository) as T
    }
}