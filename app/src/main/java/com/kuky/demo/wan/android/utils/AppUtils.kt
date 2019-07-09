package com.kuky.demo.wan.android.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * @author kuky.
 * @description
 */
object AppUtils {

    fun launchUrl(context: Context, url: String) {
        context.startActivity(
            Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}