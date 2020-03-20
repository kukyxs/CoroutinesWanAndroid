package com.kuky.demo.wan.android.ui.wxchapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.WxChapterData

/**
 * @author Taonce.
 * @description
 */

class WxChapterViewModel(private val repository: WxChapterRepository) : ViewModel() {

    var netState = MutableLiveData<NetworkState>()
    val mData = MutableLiveData<MutableList<WxChapterData>?>()

    fun getWxChapter() =
        viewModelScope.safeLaunch {
            block = {
                netState.postValue(NetworkState.LOADING)
                mData.value = repository.getWxChapter()
                netState.postValue(NetworkState.LOADED)
            }
            onError = {
                netState.postValue(NetworkState.error(it.message))
            }
        }
}