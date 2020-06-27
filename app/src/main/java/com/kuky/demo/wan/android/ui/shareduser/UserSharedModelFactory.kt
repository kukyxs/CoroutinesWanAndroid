package com.kuky.demo.wan.android.ui.shareduser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class UserSharedModelFactory(private val repository: UserSharedRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserSharedViewModel(repository) as T
    }
}