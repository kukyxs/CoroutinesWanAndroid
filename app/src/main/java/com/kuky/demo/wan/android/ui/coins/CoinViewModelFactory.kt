@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */

class CoinViewModelFactory(
    private val repository: CoinRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CoinViewModel::class.java))
            return CoinViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}