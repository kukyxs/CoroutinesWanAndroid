@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.todoedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class TodoEditViewModelFactory(
    private val repository: TodoEditRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoEditViewModel::class.java))
            return TodoEditViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}