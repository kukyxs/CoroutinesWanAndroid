@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.usershared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class UserSharedViewModelFactory(private val repository: UserSharedRepository) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserSharedViewModel::class.java))
            return UserSharedViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}