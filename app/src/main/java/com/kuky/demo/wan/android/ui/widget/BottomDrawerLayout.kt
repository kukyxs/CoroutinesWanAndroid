package com.kuky.demo.wan.android.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.utils.LogUtils
import kotlin.math.max
import kotlin.math.min

/**
 * @author kuky.
 * @description
 */
class BottomDrawerLayout : FrameLayout {
    private var mShownHeight: Float = 50f
    private val mContent: View by lazy { getChildAt(0) }
    private val mBottomMenu: View by lazy { getChildAt(1) }
    private val mDragHelper: ViewDragHelper by lazy {
        ViewDragHelper.create(this, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean = child == mBottomMenu

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                val leftBound = paddingLeft
                val rightBound = width - child.width - paddingRight
                return min(max(leftBound, left), rightBound)
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int =
                max(height - child.height, top)

            override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
                super.onEdgeTouched(edgeFlags, pointerId)
                LogUtils.error("onEdgeTouched")
            }

            override fun onEdgeLock(edgeFlags: Int): Boolean {
                LogUtils.error("onEdgeLock")
                return super.onEdgeLock(edgeFlags)
            }

            override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
                super.onEdgeDragStarted(edgeFlags, pointerId)
                mDragHelper.captureChildView(mBottomMenu, pointerId)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                mDragHelper.settleCapturedViewAt(
                    0,
                    if (yvel <= 0) height - releasedChild.height else height - mShownHeight.toInt()
                )
                invalidate()
            }
        })
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.BottomDrawerLayout)) {
            mShownHeight = getDimension(R.styleable.BottomDrawerLayout_minShownHeight, 50f)
            recycle()
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return mDragHelper.shouldInterceptTouchEvent(ev!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mDragHelper.processTouchEvent(event!!)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mDragHelper.continueSettling(true)) invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        mBottomMenu.let {
            it.layout(
                0, height - mShownHeight.toInt(),
                it.measuredWidth, height - mShownHeight.toInt() + it.measuredHeight
            )
        }

        mContent.let {
            it.layout(0, 0, it.measuredWidth, it.measuredHeight)
        }
    }

    fun setMinShownHeight(height: Float) {
        this.mShownHeight = height
        postInvalidate()
    }
}