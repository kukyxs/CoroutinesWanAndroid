package com.kuky.demo.wan.android.utils

/**
 * @author kuky.
 * @description
 */
object TimeUtils {
    fun formatDate(year: Int, month: Int, day: Int): String = "$year-${month.formatInteger()}-${day.formatInteger()}"

    private fun Int.formatInteger() = if (this < 10) "0$this" else "$this"
}