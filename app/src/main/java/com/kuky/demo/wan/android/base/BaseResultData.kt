package com.kuky.demo.wan.android.base

/**
 * @author kuky.
 * @description
 */

data class BaseResultData<T>(
    val `data`: T,
    val errorCode: Int,
    val errorMsg: String
)