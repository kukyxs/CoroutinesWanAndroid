package com.kuky.demo.wan.android.utils

import android.content.res.Resources

/**
 * @author kuky.
 * @description
 */
val screenWidth = Resources.getSystem().displayMetrics.widthPixels

val screenHeight = Resources.getSystem().displayMetrics.heightPixels

val screenDensity = Resources.getSystem().displayMetrics.density

fun Float.dp2px() = screenDensity * this + 0.5f