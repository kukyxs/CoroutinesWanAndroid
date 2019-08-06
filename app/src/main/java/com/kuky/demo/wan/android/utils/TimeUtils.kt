package com.kuky.demo.wan.android.utils

/**
 * @author kuky.
 * @description
 */
object TimeUtils {
    fun formatDate(year: Int, month: Int, day: Int): String =
        "$year-${month.let { if (it < 10) "0$it" else "$it" }}-${day.let { if (it < 10) "0$it" else it }}"
}