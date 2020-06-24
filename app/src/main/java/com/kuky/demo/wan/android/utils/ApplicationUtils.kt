package com.kuky.demo.wan.android.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 * @author kuky.
 * @description
 */
fun Context.getAppVersionName(): String {
    try {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        return packageInfo.versionName
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return ""
}

fun Context.starApp(packageName: String, fail: () -> Unit) =
    try {
        startActivity(Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            component = packageManager.getLaunchIntentForPackage(packageName)?.component
        })
        true
    } catch (e: Exception) {
        fail()
        false
    }