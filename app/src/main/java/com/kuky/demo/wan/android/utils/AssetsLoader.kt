package com.kuky.demo.wan.android.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

/**
 * @author kuky.
 * @description asserts 文件加载工具类
 */
object AssetsLoader {

    @JvmStatic
    fun getTextFromAssets(context: Context, file: String): String =
        try {
            context.resources.assets.open(file).bufferedReader().use { it.readText() }
        } catch (e: Exception) {
            ""
        }

    @JvmStatic
    fun getImageFromAssets(context: Context, file: String): Bitmap? =
        try {
            context.resources.assets.open(file).use { BitmapFactory.decodeStream(it) }
        } catch (e: Exception) {
            null
        }
}