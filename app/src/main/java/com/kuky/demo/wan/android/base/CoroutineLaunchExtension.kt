package com.kuky.demo.wan.android.base

import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description 解决协程处理网络请求不能处理异常
 */

const val ERROR_CODE_NORM = 0xFF00
const val ERROR_CODE_INIT = 0xFF10
const val ERROR_CODE_MORE = 0xFF11

class CoroutineCallback {
    var block: suspend () -> Unit = {}
    var onError: (Throwable) -> Unit = {}

    internal suspend fun onSucceed() = block()

    internal fun onFailed(throwable: Throwable) = onError.invoke(throwable)
}

fun CoroutineScope.safeLaunch(init: CoroutineCallback.() -> Unit): Job {
    val callback = CoroutineCallback().apply { this.init() }
    return launch(CoroutineExceptionHandler { _, throwable ->
        callback.onFailed(throwable)
    } + GlobalScope.coroutineContext) {
        callback.onSucceed()
    }
}

fun CoroutineScope.delayLaunch(timeMills: Long, init: CoroutineCallback.() -> Unit): Job {
    check(timeMills >= 0) { "timeMills must be positive" }
    val callback = CoroutineCallback().apply(init)
    return launch(CoroutineExceptionHandler { _, throwable ->
        callback.onFailed(throwable)
    } + GlobalScope.coroutineContext) {
        delay(timeMills)
        callback.onSucceed()
    }
}

@Suppress("FunctionName")
fun IOScope(): CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())