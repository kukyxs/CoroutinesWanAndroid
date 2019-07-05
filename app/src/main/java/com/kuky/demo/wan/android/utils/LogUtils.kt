package com.kuky.demo.wan.android.utils

import android.util.Log

/**
 * @author kuky.
 * @description
 */
object LogUtils {
    private var className: String? = null
    private var methodName: String? = null
    private var lineNumber: Int? = null

    private fun isDebuggable(): Boolean = true

    private fun createLog(logMsg: String): String {
        return "$methodName($className:$lineNumber): $logMsg"
    }

    private fun getMethodName(throwable: Throwable) {
        className = throwable.stackTrace[1].fileName
        methodName = throwable.stackTrace[1].methodName
        lineNumber = throwable.stackTrace[1].lineNumber
    }

    @JvmStatic
    fun wtf(msg: Any?) {
        if (!isDebuggable()) return
        getMethodName(Throwable())
        if (msg == null)
            Log.wtf(className, createLog("<===== empty =====>"))
        else when (msg) {
            is Int, Long, Float, Double, Boolean -> Log.wtf(className, createLog("$msg"))
            is String -> Log.wtf(className, createLog(msg))
            else -> Log.wtf(className, createLog(msg.toString()))
        }
    }

    @JvmStatic
    fun error(msg: Any?) {
        if (!isDebuggable()) return
        getMethodName(Throwable())
        if (msg == null)
            Log.e(className, createLog("<===== empty =====>"))
        else when (msg) {
            is Int, Long, Float, Double, Boolean -> Log.e(className, createLog("$msg"))
            is String -> Log.e(className, createLog(msg))
            else -> Log.e(className, createLog(msg.toString()))
        }
    }

    @JvmStatic
    fun warn(msg: Any?) {
        if (!isDebuggable()) return
        getMethodName(Throwable())
        if (msg == null)
            Log.w(className, createLog("<===== empty =====>"))
        else when (msg) {
            is Int, Long, Float, Double, Boolean -> Log.w(className, createLog("$msg"))
            is String -> Log.w(className, createLog(msg))
            else -> Log.w(className, createLog(msg.toString()))
        }
    }

    @JvmStatic
    fun info(msg: Any?) {
        if (!isDebuggable()) return
        getMethodName(Throwable())
        if (msg == null)
            Log.i(className, createLog("<===== empty =====>"))
        else when (msg) {
            is Int, Long, Float, Double, Boolean -> Log.i(className, createLog("$msg"))
            is String -> Log.i(className, createLog(msg))
            else -> Log.i(className, createLog(msg.toString()))
        }
    }

    @JvmStatic
    fun debug(msg: Any?) {
        if (!isDebuggable()) return
        getMethodName(Throwable())
        if (msg == null)
            Log.d(className, createLog("<===== empty =====>"))
        else when (msg) {
            is Int, Long, Float, Double, Boolean -> Log.d(className, createLog("$msg"))
            is String -> Log.d(className, createLog(msg))
            else -> Log.d(className, createLog(msg.toString()))
        }
    }

    @JvmStatic
    fun verbose(msg: Any?) {
        if (!isDebuggable()) return
        getMethodName(Throwable())
        if (msg == null)
            Log.v(className, createLog("<===== empty =====>"))
        else when (msg) {
            is Int, Long, Float, Double, Boolean -> Log.v(className, createLog("$msg"))
            is String -> Log.v(className, createLog(msg))
            else -> Log.v(className, createLog(msg.toString()))
        }
    }
}