package com.kuky.demo.wan.android.utils

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup

/**
 * @author kuky.
 * @description
 */
object ScreenUtils {

    @JvmStatic
    fun getScreenWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    @JvmStatic
    fun getScreenHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    @JvmStatic
    fun getScreenDensity(context: Context): Float {
        return context.resources.displayMetrics.density
    }

    @JvmStatic
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = getScreenDensity(context)
        return (dpValue * scale + 0.5f).toInt()
    }

    @JvmStatic
    fun sp2px(context: Context, spValue: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.resources.displayMetrics).toInt()
    }

    @JvmStatic
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = getScreenDensity(context)
        return (pxValue / scale + 0.5f).toInt()
    }

    @JvmStatic
    fun px2sp(context: Context, pxValue: Float): Float {
        return (pxValue / context.resources.displayMetrics.scaledDensity)
    }

    @JvmStatic
    fun getDpixel(context: Context, value: Int): Int {
        return (getScreenDensity(context) * value).toInt()
    }

    @JvmStatic
    fun getStatusBarHeight(context: Context): Int {
        val resourceRes = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceRes > 0) context.resources.getDimensionPixelSize(resourceRes) else 0
    }

    @JvmStatic
    fun setStatusBarColor(context: Context, color: String) {
        val statusBarView = View(context)
        val lp = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(context))
        statusBarView.setBackgroundColor(Color.parseColor(color))
        val contentView = (context as Activity).findViewById<ViewGroup>(android.R.id.content)
        contentView.addView(statusBarView, lp)
    }

    @JvmStatic
    fun getActionBarSize(context: Context): Int {
        val tv = TypedValue()
        return if (context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true))
            TypedValue.complexToDimensionPixelSize(tv.data, context.resources.displayMetrics)
        else 0
    }
}