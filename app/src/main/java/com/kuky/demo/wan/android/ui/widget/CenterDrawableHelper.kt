package com.kuky.demo.wan.android.ui.widget

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.widget.TextView

/**
 * @author kuky.
 * @description
 */
object CenterDrawableHelper {
    private const val DRAWABLE_START = 0
    private const val DRAWABLE_TOP = 1
    private const val DRAWABLE_END = 2
    private const val DRAWABLE_BOTTOM = 3

    private fun onCenterDraw(view: TextView, canvas: Canvas, drawable: Drawable, gravity: Int) {
        val drawablePadding = view.compoundDrawablePadding
        val fontMetrics = view.paint.fontMetrics

        val textWidth = view.paint.measureText(view.text.toString()) + drawable.intrinsicWidth
        val textHeight = fontMetrics.descent - fontMetrics.ascent + drawable.intrinsicHeight
        val total: Float

        when (gravity) {
            Gravity.START -> {
                total = textWidth + drawablePadding + view.paddingLeft + view.paddingRight
                canvas.translate((view.width - total) / 2, 0f)
            }

            Gravity.END -> {
                total = textWidth + drawablePadding + view.paddingLeft + view.paddingRight
                canvas.translate(-(view.width - total) / 2, 0f)
            }

            Gravity.TOP -> {
                total = textHeight + drawablePadding + view.paddingTop + view.paddingBottom
                canvas.translate(0f, (view.height - total) / 2)
            }

            Gravity.BOTTOM -> {
                total = textHeight + drawablePadding + view.paddingTop + view.paddingBottom
                canvas.translate(0f, -(view.height - total) / 2)
            }
        }
    }

    fun preDraw(view: TextView, canvas: Canvas?) {
        canvas?.let {
            val drawables = view.compoundDrawables

            when {
                drawables[DRAWABLE_START] != null -> {
                    view.gravity = Gravity.CENTER_VERTICAL or Gravity.START
                    onCenterDraw(view, it, drawables[DRAWABLE_START], Gravity.START)
                }

                drawables[DRAWABLE_TOP] != null -> {
                    view.gravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                    onCenterDraw(view, it, drawables[DRAWABLE_TOP], Gravity.TOP)
                }

                drawables[DRAWABLE_END] != null -> {
                    view.gravity = Gravity.CENTER_VERTICAL or Gravity.END
                    onCenterDraw(view, it, drawables[DRAWABLE_END], Gravity.END)
                }

                drawables[DRAWABLE_BOTTOM] != null -> {
                    view.gravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
                    onCenterDraw(view, it, drawables[DRAWABLE_BOTTOM], Gravity.BOTTOM)
                }
            }
        }
    }
}
