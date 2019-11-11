package com.kuky.demo.wan.android.base

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
                val first = intArrayOf(sizeOneLine)
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