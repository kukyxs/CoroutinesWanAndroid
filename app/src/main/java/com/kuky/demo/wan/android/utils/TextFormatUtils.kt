package com.kuky.demo.wan.android.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import androidx.core.text.HtmlCompat

/**
 * @author kuky.
 * @description
 */
object TextFormatUtils {
    @Suppress("DEPRECATION")
    fun renderHtmlText(description: String): Spanned =
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
            Html.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
        else Html.fromHtml(description)
}