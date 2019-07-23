package com.kuky.demo.wan.android.ui.wxchapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author Taonce.
 * @description
 */

@Suppress("UNCHECKED_CAST")
class WxChapterFactory(private val repository: WxChapterRepository) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WxChapterViewModel(repository) as T
    }
}