package com.kuky.demo.wan.android.ui.todoedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.safeLaunch

/**
 * @author kuky.
 * @description
 */
class TodoEditViewModel(private val repository: TodoEditRepository) : ViewModel() {

    fun addTodo(param: HashMap<String, Any>) {
        viewModelScope.safeLaunch {
            repository.addTodo(param)
        }
    }
}