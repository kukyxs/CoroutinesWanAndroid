package com.kuky.demo.wan.android.ui.todolist

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author kuky.
 * @description
 */
class UpdateListViewModel : ViewModel() {

    val needUpdate = MutableLiveData<Boolean>()

    init {
        needUpdate.value = false
    }
}