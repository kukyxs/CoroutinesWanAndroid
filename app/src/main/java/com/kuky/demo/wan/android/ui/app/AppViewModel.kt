package com.kuky.demo.wan.android.ui.app

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.kuky.demo.wan.android.base.BaseViewModel
import com.kuky.demo.wan.android.base.UiState
import com.kuky.demo.wan.android.helper.SingleLiveEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * @author kuky.
 * @description
 */
class AppViewModel(application: Application) : BaseViewModel(application) {

    private val _wholeState = MutableStateFlow<UiState>(UiState.Succeed())
    val wholeState: StateFlow<UiState> = _wholeState

    val reloadHomeData = MutableLiveData<Boolean>()

    val reloadCollectWebsite = SingleLiveEvent<Boolean>()

    val needUpdateTodoList = SingleLiveEvent<Boolean>()

    fun showLoading() {
        _wholeState.value = UiState.Loading
    }

    fun dismissLoading() {
        _wholeState.value = UiState.Succeed()
    }
}