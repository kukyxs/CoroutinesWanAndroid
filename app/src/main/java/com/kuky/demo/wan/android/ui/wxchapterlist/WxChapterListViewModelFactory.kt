@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class WxChapterListViewModelFactory(
    private val repository: WxChapterListRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WxChapterListViewModel::class.java))
            return WxChapterListViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}