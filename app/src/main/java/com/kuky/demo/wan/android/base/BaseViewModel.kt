@file:Suppress("MemberVisibilityCanBePrivate")

package com.kuky.demo.wan.android.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.helper.KLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

/**
 * @author kuky.
 * @description
 */
open class BaseViewModel(
    application: Application
) : AndroidViewModel(application), KLogger {

    protected val mContext by lazy {
        getApplication<WanApplication>().applicationContext
    }

    // ui 状态管理，如果有多余的需要管理请另外定义
    // 如果多个 fragment share viewModel，可分别定义对应的 uiState
    // doRequest 传入对应的 uiState 即可
    private val _uiState = MutableStateFlow<UiState>(UiState.Create)
    val uiState: StateFlow<UiState> = _uiState

    suspend fun <T> Flow<T>.doRequest(
        uiFlow: MutableStateFlow<UiState> = _uiState,
        scope: CoroutineScope = viewModelScope
    ) = this.onStart {
        uiFlow.value = UiState.Loading
    }.catch {
        uiFlow.value = UiState.Error(it)
    }.onCompletion {
        uiFlow.value = UiState.Succeed()
    }.stateIn(scope)

    //
    open fun changeUiState(state: UiState, targetState: MutableStateFlow<UiState> = _uiState) {
        targetState.value = state
    }

    // 根据 PagerAdapter LoadState 设置对应的 uiState
    // Paging 的加载状态只能通过 Adapter 进行读取，所以通过对外暴露方法进行设置，无法直接在 viewModel 设置
    fun listenPagerLoadState(
        loadState: CombinedLoadStates,
        targetState: MutableStateFlow<UiState> = _uiState,
        emptyPredicate: () -> Boolean = { false }
    ) {
        when (val state = loadState.refresh) {
            is LoadState.Loading -> changeUiState(UiState.Loading, targetState)

            is LoadState.Error -> changeUiState(UiState.Error(state.error), targetState)

            else -> changeUiState(UiState.Succeed(emptyPredicate()), targetState)
        }
    }
}