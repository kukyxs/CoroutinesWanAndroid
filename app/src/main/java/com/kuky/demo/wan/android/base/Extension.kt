package com.kuky.demo.wan.android.base

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.kuky.demo.wan.android.WanApplication
import kotlinx.coroutines.*
import org.jetbrains.anko.toast
import kotlin.math.min

/**
 * @author kuky.
 * @description
 */
fun Context.stringValue(@StringRes stringRes: Int) = resources.getString(stringRes)


suspend fun <T> BaseResultData<T>.handleResult(
    fail: suspend (String) -> Unit = { WanApplication.instance.toast(it) },
    ok: suspend (T) -> Unit = {}
) {
    if (errorCode == 0) ok(data)
    else fail(errorMsg)
}

@Suppress("DEPRECATION")
fun String.renderHtml(): String =
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        Html.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    else Html.fromHtml(this).toString()