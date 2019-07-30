package com.kuky.demo.wan.android.base

import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.kuky.demo.wan.android.R

/**
 * @author kuky.
 * @description
 */
@Suppress("LeakingThis")
abstract class BasePopupWindow<VB : ViewDataBinding>(
    context: Context,
    animStyle: Int = R.style.DialogPushInOutAnimation,
    width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
    height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
) : PopupWindow(context) {

    protected val mBinding: VB

    init {
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutId(), null, false)
        contentView = mBinding.root
        initPopup()
        isFocusable = true
        animationStyle = animStyle
        setBackgroundDrawable(ColorDrawable(0))
        setWidth(width)
        setHeight(height)
        setOnDismissListener {
            setBackgroundAlpha(alphaWhenDismissed())
        }
    }

    private fun setBackgroundAlpha(alpha: Float) =
        (mBinding.root.context as Activity).let {
            it.window.attributes = it.window.attributes
                .apply {
                    this.alpha = alpha
                }
        }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int, gravity: Int) {
        setBackgroundAlpha(alphaWhenShowing())
        super.showAsDropDown(anchor, xoff, yoff, gravity)
    }

    override fun showAsDropDown(anchor: View?) {
        setBackgroundAlpha(alphaWhenShowing())
        super.showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View?, xoff: Int, yoff: Int) {
        setBackgroundAlpha(alphaWhenShowing())
        super.showAsDropDown(anchor, xoff, yoff)
    }

    override fun showAtLocation(parent: View?, gravity: Int, x: Int, y: Int) {
        setBackgroundAlpha(alphaWhenShowing())
        super.showAtLocation(parent, gravity, x, y)
    }

    protected open fun alphaWhenShowing() = 0.5f

    protected open fun alphaWhenDismissed() = 1.0f

    abstract fun getLayoutId(): Int

    abstract fun initPopup()
}