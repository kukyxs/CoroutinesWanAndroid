package com.kuky.demo.wan.android.base

import android.content.Context
import android.os.Build
import android.text.Html
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

/**
 * @author kuky.
 * @description
 */

fun RecyclerView.scrollToTop(sizeOneLine: Int = 2, threshold: Int = 10) {

    when (val manager = layoutManager) {
        is LinearLayoutManager -> {
            manager.let {
                val first = it.findFirstCompletelyVisibleItemPosition()
                if (first == 0) return@let

                manager.scrollToPositionWithOffset(min(first, threshold), 0)
                GlobalScope.launch(Dispatchers.Main) {
                    delay(10)
                    manager.smoothScrollToPosition(this@scrollToTop, RecyclerView.State(), 0)
                }
            }
        }

        is GridLayoutManager -> {
            manager.let {
                val first = it.findFirstCompletelyVisibleItemPosition()
                if (first == 0) return@let

                manager.scrollToPositionWithOffset(min(first, threshold), 0)
                GlobalScope.launch(Dispatchers.Main) {
                    delay(10)
                    manager.smoothScrollToPosition(this@scrollToTop, RecyclerView.State(), 0)
                }
            }
        }

        is StaggeredGridLayoutManager -> {
            manager.let {
                val first = IntArray(sizeOneLine)
                it.findFirstCompletelyVisibleItemPositions(first)
                if (first[0] == 0) return@let

                manager.scrollToPositionWithOffset(min(first[0], threshold), 0)
                GlobalScope.launch(Dispatchers.Main) {
                    delay(10)
                    manager.smoothScrollToPosition(this@scrollToTop, RecyclerView.State(), 0)
                }
            }
        }
    }
}

@Suppress("DEPRECATION")
fun String.renderHtml(): String =
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        Html.fromHtml(this, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
    else Html.fromHtml(this).toString()


fun EditText.hideSoftInput() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.hideSoftInputFromWindow(windowToken, 0)
}

fun EditText.clearText() {
    setText("")
}