package com.kuky.demo.wan.android.base

/**
 * @author kuky
 * @description 动态权限申请监听
 */
interface PermissionListener {
    fun onGranted()

    fun onDenied(deniedPermissions: List<String>)
}
