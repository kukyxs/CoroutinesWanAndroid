package com.kuky.demo.wan.android.base

import android.annotation.SuppressLint
import android.os.Handler
import android.view.MotionEvent
import android.view.View

/**
 * @author kuky.
 * @description
 */

typealias OnSingleTap = () -> Unit

typealias OnDoubleTap = () -> Unit

private const val INTERVAL = 300L

class DoubleClickListener(
    private val singleTap: OnSingleTap? = null,
    private val doubleTap: OnDoubleTap? = null
) : View.OnTouchListener {
    private var count = 0
    private val handler = Handler()

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            count++
            handler.postDelayed({
                if (count == 1) singleTap?.invoke()
                else if (count == 2) doubleTap?.invoke()
                handler.removeCallbacksAndMessages(0)
                count = 0
            }, INTERVAL)
        }

        return false
    }
}