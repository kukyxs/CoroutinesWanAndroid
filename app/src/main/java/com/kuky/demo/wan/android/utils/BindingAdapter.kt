package com.kuky.demo.wan.android.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/**
 * @author kuky.
 * @description
 */

@BindingAdapter("bind:loadImage")
fun loadImage(view: ImageView, url: String) {
    Glide.with(view.context)
        .load(url)
        .apply(RequestOptions.centerCropTransform())
        .into(view)
}