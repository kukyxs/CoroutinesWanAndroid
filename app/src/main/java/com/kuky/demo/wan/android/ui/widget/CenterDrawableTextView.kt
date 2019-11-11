package com.kuky.demo.wan.android.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * @author kuky.
 * @description
 */

open class CenterDrawableTextView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    override fun onDraw(canvas: Canvas?) {
        CenterDrawableHelper.preDraw(this, canvas)
        super.onDraw(canvas)
    }
}