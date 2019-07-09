package com.kuky.demo.wan.android.ui.wxchapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.data.wxchapter.WxChapterRepository
import com.kuky.demo.wan.android.entity.WxChapterData
import kotlinx.coroutines.launch

/**
 * @author Taonce.
 * @description
 */

class WxChapterViewModel(private val repository: WxChapterRepository) : ViewModel() {
    val isRefresh = MutableLiveData<Boolean>()
    val mData = mutableListOf<WxChapterData>()

    fun getWxChapter() = viewModelScope.launch {
        mData.addAll(repository.getWxChapter().data)
        isRefresh.value = !mData.isEmpty()
    }
}