package com.kuky.demo.wan.android.ui.wxchapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.WxChapterData

/**
 * @author Taonce.
 * @description
 */

class WxChapterViewModel(private val repository: WxChapterRepository) : ViewModel() {
    val isRefresh = MutableLiveData<Boolean>()
    val mData = mutableListOf<WxChapterData>()

    init {
        isRefresh.value = false
    }

    fun getWxChapter() = viewModelScope.safeLaunch {
        if (!isRefresh.value!!) {
            mData.addAll(repository.getWxChapter().data)
            isRefresh.value = true
        }
    }
}