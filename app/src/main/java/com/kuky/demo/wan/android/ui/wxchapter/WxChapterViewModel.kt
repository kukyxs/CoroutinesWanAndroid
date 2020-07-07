package com.kuky.demo.wan.android.ui.wxchapter

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */

class WxChapterViewModel(
    private val repository: WxChapterRepository
) : ViewModel() {

    fun getWxChapterList() = flow {
        emit(repository.getWxChapter())
    }
}