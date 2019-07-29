package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author kuky.
 * @description
 */
class WxChapterListModelFactory(private val repository: WxChapterListRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WxChapterListViewModel(repository) as T
    }
}