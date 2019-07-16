package com.kuky.demo.wan.android.base

/**
 * @author kuky.
 * @description
 */

const val CODE_SUCCEED = 0xFF01
const val CODE_FAILED = 0xFF02

data class ResultBack(val code: Int, val message: String)