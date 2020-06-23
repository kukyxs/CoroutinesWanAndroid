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
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import kotlin.math.min

/**
 * @author kuky.
 * @description
 */
suspend fun <T> workOnMain(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.Main) { block() }
}

suspend fun <T> workOnIO(block: suspend CoroutineScope.() -> T) {
    withContext(Dispatchers.IO) { block() }
}

fun TabLayout.setupWithViewPager2(viewPager2: ViewPager2, titles: MutableList<String>): TabLayoutMediator =
    TabLayoutMediator(this, viewPager2) { tab, position ->
        tab.text = titles[position]
    }.apply { attach() }

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

fun EditText.showSoftInput() {
    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)
        ?.showSoftInput(this, InputMethodManager.SHOW_FORCED)
}

fun EditText.clearText() {
    setText("")
}

fun ViewPager.onChange(
    stateChange: ((Int) -> Unit)? = null, scrolled: ((Int, Float, Int) -> Unit)? = null,
    selected: ((Int) -> Unit)? = null
) {
    addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            stateChange?.invoke(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            scrolled?.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            selected?.invoke(position)
        }
    })
}