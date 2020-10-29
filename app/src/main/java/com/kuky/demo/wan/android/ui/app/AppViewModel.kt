package com.kuky.demo.wan.android.ui.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kuky.demo.wan.android.base.SingleLiveEvent

/**
 * @author kuky.
 * @description
 */
class AppViewModel : ViewModel() {
    val showLoadingProgress = SingleLiveEvent<Boolean>()

    val reloadHomeData = MutableLiveData<Boolean>()

    val reloadCollectWebsite = SingleLiveEvent<Boolean>()

    val needUpdateTodoList = SingleLiveEvent<Boolean>()

    fun showLoading() = showLoadingProgress.postValue(true)

    fun dismissLoading() = showLoadingProgress.postValue(false)
}