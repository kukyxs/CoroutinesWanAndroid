package com.kuky.demo.wan.android.data

import android.content.Context
import android.text.TextUtils
import com.kuky.demo.wan.android.utils.SharePreferencesUtils

/**
 * @author kuky.
 * @description
 */
object SearchHistoryUtils {
    private const val SHARE_KEY_HISTORY = "wan.search.history"

    fun hasHistory(context: Context) =
        SharePreferencesUtils.getString(context, SHARE_KEY_HISTORY).isNotEmpty()

    /**
     * 添加搜索记录
     */
    fun saveHistory(context: Context, keyword: String) {
        val content = SharePreferencesUtils.getString(
            context,
            SHARE_KEY_HISTORY
        )

        val store = if (keyword.contains(",")) keyword.replace(",", " ") else keyword

        val list = when {
            content.contains(",") -> {
                val results = content.split(",") as ArrayList
                if (results.contains(store)) results.remove(store)
                results.add(0, store)
                results
            }
            content.isNotBlank() -> {
                if (TextUtils.equals(store, content))
                    arrayListOf(store)
                else
                    arrayListOf(store, content)
            }

            else -> arrayListOf(store)
        }

        saveListAsString(context, list)
    }

    /**
     * 删除搜索记录
     */
    fun removeKeyword(context: Context, keyword: String) {
        val content = SharePreferencesUtils.getString(
            context,
            SHARE_KEY_HISTORY
        )

        val list = when {
            content.contains(",") -> {
                val results = content.split(",") as ArrayList
                if (results.contains(keyword)) results.remove(keyword)
                results
            }

            content.isNotBlank() ->
                if (TextUtils.equals(content, keyword)) arrayListOf()
                else arrayListOf(content)

            else -> arrayListOf()
        }

        saveListAsString(context, list)
    }

    /**
     * 获取全部搜索记录
     */
    fun fetchHistoryKeys(context: Context): ArrayList<String>? {
        val content = SharePreferencesUtils.getString(
            context,
            SHARE_KEY_HISTORY
        )

        return when {
            content.contains(",") -> content.split(",") as ArrayList<String>
            content.isBlank() -> null
            else -> arrayListOf(content)
        }
    }

    /**
     * 列表转字符
     */
    private fun saveListAsString(context: Context, strings: List<String>) {
        val sb = StringBuilder()

        strings.forEach { sb.append(it).append(",") }

        val result =
            if (sb.contains(","))
                sb.substring(0, sb.length - 1)
            else sb.toString()

        SharePreferencesUtils.saveString(
            context,
            SHARE_KEY_HISTORY,
            result
        )
    }
}