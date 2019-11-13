package com.kuky.demo.wan.android.ui.usersharelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class UserShareListModelFactory(private val repository: UserShareListRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserShareListViewModel(repository) as T
    }
}