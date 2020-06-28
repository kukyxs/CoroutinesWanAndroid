package com.kuky.demo.wan.android.base

/**
 * @author kuky.
 * @description
 */

enum class NetworkState {
    RUNNING, SUCCESS, FAILED
}

data class RequestState(
    val state: NetworkState,
    val msg: String? = null,
    val code: Int? = null
) {
    companion object {
        val LOADED = RequestState(NetworkState.SUCCESS)
        val LOADING = RequestState(NetworkState.RUNNING)
        fun error(msg: String? = "", code: Int = 400) = RequestState(NetworkState.FAILED, msg ?: "unknown error", code)
    }
}