package com.kuky.demo.wan.android.ui.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author kuky.
 * @description
 */

class AppViewModel : ViewModel() {
    val showLoadingProgress = MutableLiveData<Boolean>()

    fun showLoading() = showLoadingProgress.postValue(true)

    fun dismissLoading() = showLoadingProgress.postValue(false)
}