package com.kuky.demo.wan.android.utils

import android.content.Context
import android.graphics.BitmapFactory

/**
 * @author kuky.
 * @description asserts 文件加载工具类
 */

fun Context.loadTextFromAssets(file: String): String =
    try {
        resources.assets.open(file).bufferedReader().use { it.readText() }
    } catch (e: Exception) {
        ""
    }

fun Context.loadImageFromAssets(file: String) = try {
    resources.assets.open(file).use { BitmapFactory.decodeStream(it) }
} catch (e: Exception) {
    null
}