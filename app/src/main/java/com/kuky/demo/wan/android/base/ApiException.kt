package com.kuky.demo.wan.android.base

/**
 * @author kuky.
 * @description
 */
class ApiException(
    val msg: String = "error on data respond"
) : Exception(msg)