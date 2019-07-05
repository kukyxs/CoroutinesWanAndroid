package com.kuky.demo.wan.android.utils

import android.view.View
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs
import kotlin.math.max

/**
 * @author kuky.
 * @description
 */
class GalleryTransformer : ViewPager.PageTransformer {

    companion object {
        private const val MIN_SCALE = 0.85f
    }

    override fun transformPage(page: View, position: Float) {
        val factory = max(MIN_SCALE, 1 - abs(position))
        val degree = abs(position) * 8

        page.apply {
            when {
                position <= -1 -> {
                    scaleX = MIN_SCALE
                    scaleY = MIN_SCALE
                    rotationY = -degree
                }

                position > -1 && position < 0 -> {
                    scaleX = factory
                    scaleY = factory
                    rotationY = -degree
                }

                position >= 0 && position < 1 -> {
                    scaleX = factory
                    scaleY = factory
                    rotationY = degree
                }

                position >= 1 -> {
                    scaleX = MIN_SCALE
                    scaleY = MIN_SCALE
                    rotationY = degree
                }
            }
        }
    }
}