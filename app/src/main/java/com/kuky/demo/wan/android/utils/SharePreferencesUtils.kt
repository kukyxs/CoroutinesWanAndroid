package com.kuky.demo.wan.android.utils

import android.content.Context
import androidx.core.content.edit

/**
 * @author kuky.
 * @description
 */
object SharePreferencesUtils {
    private const val SHARED_PREFERENCES_NAME = "com.base.share.preference"

    @JvmStatic
    fun saveString(context: Context, key: String, value: String) {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sp.edit { putString(key, value) }
    }

    @JvmStatic
    fun getString(context: Context, key: String, default: String = ""): String {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sp.getString(key, default) ?: ""
    }

    @JvmStatic
    fun saveInteger(context: Context, key: String, value: Int) {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sp.edit { putInt(key, value) }
    }

    @JvmStatic
    fun getInteger(context: Context, key: String, default: Int = 0): Int {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sp.getInt(key, default)
    }

    @JvmStatic
    fun saveLong(context: Context, key: String, value: Long) {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sp.edit { putLong(key, value) }
    }

    @JvmStatic
    fun getLong(context: Context, key: String, default: Long = 0L): Long {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sp.getLong(key, default)
    }

    @JvmStatic
    fun saveFloat(context: Context, key: String, value: Float) {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sp.edit { putFloat(key, value) }
    }

    @JvmStatic
    fun getFloat(context: Context, key: String, default: Float = 0F): Float {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sp.getFloat(key, default)
    }

    @JvmStatic
    fun saveBoolean(context: Context, key: String, value: Boolean) {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sp.edit { putBoolean(key, value) }
    }

    @JvmStatic
    fun getBoolean(context: Context, key: String, default: Boolean = false): Boolean {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sp.getBoolean(key, default)
    }

    @JvmStatic
    fun saveStringSet(context: Context, key: String, value: MutableSet<String>?) {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        sp.edit { putStringSet(key, value) }
    }

    @JvmStatic
    fun getStringSet(context: Context, key: String): MutableSet<String>? {
        val sp = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return sp.getStringSet(key, null)
    }
}