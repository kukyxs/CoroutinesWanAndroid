@file:Suppress("UNCHECKED_CAST")

package com.kuky.demo.wan.android.ui.wxchapter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * @author Taonce.
 * @description
 */

class WxChapterViewModelFactory(
    private val repository: WxChapterRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WxChapterViewModel::class.java))
            return WxChapterViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel Type")
    }
}