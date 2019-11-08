package com.kuky.demo.wan.android.base

/**
 * @author kuky.
 * @description 用于 paging 数据出错处理
 */

const val PAGING_THROWABLE_LOAD_CODE_INITIAL = 0xFF01

const val PAGING_THROWABLE_LOAD_CODE_AFTER = 0xFF10

const val PAGING_THROWABLE_LOAD_CODE_BEFORE = 0xFF11

typealias PagingThrowableHandler = (Int, Throwable) -> Unit