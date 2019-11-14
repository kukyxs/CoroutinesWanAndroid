package com.kuky.demo.wan.android.base

/**
 * @author kuky.
 * @description
 */

enum class State {
    RUNNING, SUCCESS, FAILED
}

data class NetworkState(
    val state: State,
    val msg: String? = null,
    val code: Int? = null
) {
    companion object {
        val LOADED = NetworkState(State.SUCCESS)
        val LOADING = NetworkState(State.RUNNING)
        fun error(msg: String?, code: Int = ERROR_CODE_NORM) = NetworkState(State.FAILED, msg ?: "unknown error", code)
    }
}