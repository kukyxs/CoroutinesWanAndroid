@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class TodoListViewModelFactory(
    private val repository: TodoListRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoListViewModel::class.java))
            return TodoListViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}