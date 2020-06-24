package com.kuky.demo.wan.android.utils

import android.content.Context
import androidx.core.content.edit

/**
 * @author kuky.
 * @description
 */
private const val SHARED_PREFERENCES_NAME = "com.base.share.preference"

fun Context.saveString(key: String, value: String) {
    val sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    sp.edit { putString(key, value) }
}

fun Context.getString(key: String, default: String = ""): String {
    val sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sp.getString(key, default) ?: ""
}

fun Context.saveInteger(key: String, value: Int) {
    val sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    sp.edit { putInt(key, value) }
}

fun Context.getInteger(key: String, default: Int = 0): Int {
    val sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sp.getInt(key, default)
}

fun Context.saveBoolean(key: String, value: Boolean) {
    val sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    sp.edit { putBoolean(key, value) }
}

fun Context.getBoolean(key: String, default: Boolean = false): Boolean {
    val sp = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    return sp.getBoolean(key, default)
}