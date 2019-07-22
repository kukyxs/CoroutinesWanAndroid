package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description 用于通用返回的情况
 */
class BasicResultData(
    val `data`: Any,
    val errorCode: Int,
    val errorMsg: String
)