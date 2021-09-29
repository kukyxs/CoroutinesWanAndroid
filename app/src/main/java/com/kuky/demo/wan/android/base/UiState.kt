package com.kuky.demo.wan.android.base

/**
 * @author kuky.
 * @description
 */
sealed class UiState {
    object Create : UiState()

    object Loading : UiState()

    data class Succeed(val isEmpty: Boolean = false) : UiState()

    data class Error(val throwable: Throwable = ApiException()) : UiState()
}


