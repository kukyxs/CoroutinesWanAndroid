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
    val mData = MutableLiveData<MutableList<WxChapterData>?>()

    fun getWxChapter() = viewModelScope.safeLaunch {
        mData.value = repository.getWxChapter()
    }
}