package com.kuky.demo.wan.android.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatTextView
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.utils.LogUtils


/**
 * @author kuky.
 * @description
 */
class DrawableTextView : AppCompatTextView {

    private var mDrawables: Array<Drawable?> = Array(4) { null }
    private var mWidths: IntArray = IntArray(4)
    private var mHeights: IntArray = IntArray(4)

    @IntDef(LEFT, TOP, RIGHT, BOTTOM)
    @Retention(AnnotationRetention.SOURCE)
    annotation class DrawGravity {}

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {

        with(context.obtainStyledAttributes(attrs, R.styleable.DrawableTextView)) {
            mDrawables[0] = getDrawable(R.styleable.DrawableTextView_leftDrawable)
            mDrawables[1] = getDrawable(R.styleable.DrawableTextView_topDrawable)
            mDrawables[2] = getDrawable(R.styleable.DrawableTextView_rightDrawable)
            mDrawables[3] = getDrawable(R.styleable.DrawableTextView_bottomDrawable)

            mWidths[0] = getDimensionPixelSize(R.styleable.DrawableTextView_leftDrawableWidth, 0)
            mWidths[1] = getDimensionPixelSize(R.styleable.DrawableTextView_topDrawableWidth, 0)
            mWidths[2] = getDimensionPixelSize(R.styleable.DrawableTextView_rightDrawableWidth, 0)
            mWidths[3] = getDimensionPixelSize(R.styleable.DrawableTextView_bottomDrawableWidth, 0)

            mHeights[0] = getDimensionPixelSize(R.styleable.DrawableTextView_leftDrawableHeight, 0)
            mHeights[1] = getDimensionPixelSize(R.styleable.DrawableTextView_topDrawableHeight, 0)
            mHeights[2] = getDimensionPixelSize(R.styleable.DrawableTextView_rightDrawableHeight, 0)
            mHeights[3] = getDimensionPixelSize(R.styleable.DrawableTextView_bottomDrawableHeight, 0)
            recycle()
        }
    }

    fun setDrawable(@DrawGravity gravity: Int, drawable: Drawable, width: Int, height: Int) {
        mDrawables[gravity] = drawable
        mWidths[gravity] = width
        mHeights[gravity] = height
        postInvalidate()
    }

    fun setDrawables(drawables: Array<Drawable?>, widths: IntArray, heights: IntArray) {
        if (drawables.size == 4 && widths.size == 4 && heights.size == 4) {
            this.mDrawables = drawables
            this.mWidths = widths
            this.mHeights = heights
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        val drawablePadding = compoundDrawablePadding
        translateText(canvas, drawablePadding)

        val centerX = (width + paddingLeft - paddingRight) / 2
        val centerY = (height + paddingTop - paddingBottom) / 2

        val halfTextWidth = paint.measureText(if (text.isNotEmpty()) text.toString() else hint.toString()) / 2f
        val fontMetrics = paint.fontMetrics
        val halfTextHeight = (fontMetrics.descent - fontMetrics.ascent) / 2

        mDrawables[0]?.let { drawable ->
            val left = (centerX - drawablePadding - halfTextWidth - mWidths[0]).toInt()
            val top = centerY - mHeights[0] / 2
            drawable.setBounds(left, top, left + mWidths[0], top + mHeights[0])

            canvas?.let {
                it.save()
                drawable.draw(it)
                it.restore()
            }
        }

        mDrawables[2]?.let { drawable ->
            val left = (centerX + halfTextWidth + drawablePadding).toInt()
            val top = centerY - mHeights[2] / 2
            drawable.setBounds(left, top, left + mWidths[2], top + mHeights[2])

            canvas?.let {
                it.save()
                drawable.draw(it)
                it.restore()
            }
        }

        mDrawables[1]?.let { drawable ->
            val left = centerX - mWidths[1] / 2
            val bottom = (centerY - halfTextHeight - drawablePadding).toInt()
            drawable.setBounds(left, bottom - mHeights[1], left + mWidths[1], bottom)

            canvas?.let {
                it.save()
                drawable.draw(it)
                it.restore()
            }
        }

        mDrawables[3]?.let { drawable ->
            val left = centerX - mWidths[3] / 2
            val top = (centerY + halfTextHeight + drawablePadding).toInt()
            drawable.setBounds(left, top, left + mWidths[3], top + mHeights[3])

            canvas?.let {
                it.save()
                drawable.draw(it)
                it.restore()
            }
        }
    }

    private fun translateText(canvas: Canvas?, drawablePadding: Int) {
        val translateWidth = when {
            mDrawables[0] != null && mDrawables[2] != null -> (mWidths[0] - mWidths[2]) / 2

            mDrawables[0] != null -> (mWidths[0] + drawablePadding) / 2

            mDrawables[2] != null -> -(mWidths[2] + drawablePadding) / 2

            else -> 0
        }

        val translateHeight = when {
            mDrawables[1] != null && mDrawables[3] != null -> (mHeights[1] - mHeights[3]) / 2

            mDrawables[1] != null -> (mHeights[1] + drawablePadding) / 2

            mDrawables[3] != null -> -(mHeights[3] - drawablePadding) / 2

            else -> 0
        }

        canvas?.translate(translateWidth.toFloat(), translateHeight.toFloat())
    }

    companion object {
        const val LEFT = 0
        const val TOP = 1
        const val RIGHT = 2
        const val BOTTOM = 3
    }
}