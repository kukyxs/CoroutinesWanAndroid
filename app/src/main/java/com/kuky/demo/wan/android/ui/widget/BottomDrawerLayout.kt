package com.kuky.demo.wan.android.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.customview.widget.ViewDragHelper
import com.kuky.demo.wan.android.R
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
        ViewDragHelper.create(this, 1.0f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean = child == mBottomMenu

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                val leftBound = paddingLeft
                val rightBound = width - child.width - paddingRight
                return min(max(leftBound, left), rightBound)
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int =
                max(height - child.height, top)

            override fun onEdgeDragStarted(edgeFlags: Int, pointerId: Int) {
                super.onEdgeDragStarted(edgeFlags, pointerId)
                mDragHelper.captureChildView(mBottomMenu, pointerId)
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                val top = if (yvel < 0 ||
                    (yvel == 0f && (height - releasedChild.top >= releasedChild.height / 2))
                ) height - releasedChild.height else height - mShownHeight.toInt()

                mDragHelper.settleCapturedViewAt(0, top)
                invalidate()
            }

            override fun onViewDragStateChanged(state: Int) {
                super.onViewDragStateChanged(state)

                // 当静止后，判断是否为打开状态
                if (state == ViewDragHelper.STATE_IDLE) {
                    if (mBottomMenu.top == height - mBottomMenu.height) {
                        mIsOpened = true
                    } else if (mBottomMenu.top == height - mShownHeight.toInt()) {
                        mIsOpened = false
                    }
                }
            }
        })
    }

    private var mIsOpened = false

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        with(context.obtainStyledAttributes(attrs, R.styleable.BottomDrawerLayout)) {
            mShownHeight = getDimension(R.styleable.BottomDrawerLayout_minShownHeight, 50f)
            recycle()
        }
    }

    /**
     * 解决 RecyclerView 和 ViewDragHelper 滑动冲突
     */
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        var canDrag = false

        ev?.let {
            if (it.action == MotionEvent.ACTION_DOWN) {
                val x = it.x.toInt()
                val y = it.y.toInt()

                canDrag = x in mBottomMenu.left..mBottomMenu.right
                        && y in mBottomMenu.top..(mBottomMenu.top + mShownHeight.toInt())
            }
        }

        return if (canDrag) {
            mDragHelper.processTouchEvent(ev!!)
            super.onInterceptTouchEvent(ev)
        } else mDragHelper.shouldInterceptTouchEvent(ev!!)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mDragHelper.processTouchEvent(event!!)
        return true
    }

    override fun computeScroll() {
        super.computeScroll()
        if (mDragHelper.continueSettling(true)) invalidate()
    }

    fun animClosed() {
        mDragHelper.smoothSlideViewTo(mBottomMenu, 0, height - mShownHeight.toInt())
        invalidate()
    }

    fun animOpen() {
        mDragHelper.smoothSlideViewTo(mBottomMenu, 0, height - mBottomMenu.height)
        invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        mBottomMenu.let {
            val menuTop = if (mIsOpened) height - it.height else height - mShownHeight.toInt()
            val menuBottom = menuTop + it.measuredHeight

            it.layout(0, menuTop, it.measuredWidth, menuBottom)
        }

        mContent.let {
            it.layout(0, 0, it.measuredWidth, it.measuredHeight)
        }
    }
}