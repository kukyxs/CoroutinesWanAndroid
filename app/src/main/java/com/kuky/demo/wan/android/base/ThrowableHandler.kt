package com.kuky.demo.wan.android.base

import android.util.Log

/**
 * @author kuky.
 * @description 用于 paging 数据出错处理
 */

const val PAGING_THROWABLE_LOAD_CODE_INITIAL = 0xFF01

const val PAGING_THROWABLE_LOAD_CODE_AFTER = 0xFF10

const val PAGING_THROWABLE_LOAD_CODE_BEFORE = 0xFF11

typealias PagingThrowableHandler = (Int, Throwable) -> Unit

typealias CoroutineThrowableHandler = (Throwable) -> Unit

val DEFAULT_HANDLER = object : CoroutineThrowableHandler {
    override fun invoke(p1: Throwable) {
        Log.e("TAG", "Throwable", p1)
    }
}