package com.kuky.demo.wan.android.extension

import kotlinx.coroutines.*

/**
 * @author kuky.
 * @description 解决协程处理网络请求不能处理异常
 */
suspend fun <T> workOnMain(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.Main) { block() }
}

suspend fun <T> workOnIO(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.IO) { block() }
}

data class CoroutineCallback(
    var initDispatcher: CoroutineDispatcher? = null,
    var block: suspend () -> Unit = {},
    var onError: (Throwable) -> Unit = {}
)

fun CoroutineScope.safeLaunch(init: CoroutineCallback.() -> Unit): Job {
    val callback = CoroutineCallback().apply { this.init() }
    return launch(CoroutineExceptionHandler { _, throwable ->
        callback.onError(throwable)
    } + (callback.initDispatcher ?: GlobalScope.coroutineContext)) {
        callback.block()
    }
}

fun CoroutineScope.delayLaunch(timeMills: Long, init: CoroutineScope.() -> Unit): Job {
    check(timeMills >= 0) { "timeMills must be positive" }
    return launch {
        delay(timeMills)
        init()
    }
}

fun CoroutineScope.repeatLaunch(
    interval: Long, init: CoroutineScope.(Int) -> Unit,
    repeatCount: Int = Int.MAX_VALUE, delayTime: Long = 0L
): Job {
    check(interval > 0) { "timeDelta must be positive" }
    check(repeatCount > 0) { "repeat count must be positive" }

    return launch {
        if (delayTime > 0) delay(delayTime)

        repeat(repeatCount) {
            init(it)
            delay(interval)
        }
    }
}

@Suppress("FunctionName")
fun IOScope(): CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())