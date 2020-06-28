@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.userarticles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class UserArticleViewModelFactory(
    private val repository: UserArticleRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserArticleViewModel::class.java))
            return UserArticleViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}