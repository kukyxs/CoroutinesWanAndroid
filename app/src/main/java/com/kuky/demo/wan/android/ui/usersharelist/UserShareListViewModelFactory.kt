@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.usersharelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class UserShareListViewModelFactory(
    private val repository: UserShareListRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserShareListViewModel::class.java))
            return UserShareListViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}