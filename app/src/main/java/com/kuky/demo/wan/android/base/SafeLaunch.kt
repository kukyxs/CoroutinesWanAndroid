package com.kuky.demo.wan.android.base

import com.kuky.demo.wan.android.utils.LogUtils
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * @author kuky.
 * @description 解决协程处理网络请求不能处理异常
 */
typealias CoroutineThrowableHandler = (Throwable) -> Unit

private fun coroutineExceptionHandler(
    throwableHandler: CoroutineThrowableHandler? = null
): CoroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
    LogUtils.info(throwable.toString())
    throwableHandler?.invoke(throwable)
}

private fun coroutineExceptionContext(
    throwableHandler: CoroutineThrowableHandler? = null
): CoroutineContext = coroutineExceptionHandler(throwableHandler) + GlobalScope.coroutineContext

fun CoroutineScope.safeLaunch(
    throwableHandler: CoroutineThrowableHandler? = null,
    block: suspend () -> Unit
): Job = launch(coroutineExceptionContext(throwableHandler)) {
    block()
}