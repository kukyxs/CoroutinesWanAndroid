package com.kuky.demo.wan.android.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.kuky.demo.wan.android.entity.BannerData
import com.youth.banner.Banner
import com.youth.banner.loader.ImageLoader

/**
 * @author kuky.
 * @description
 */

@BindingAdapter(value = ["bind:loadImage", "bind:placeHolder", "bind:error"], requireAll = false)
fun loadImage(view: ImageView, url: String, placeholder: Drawable, errorHolder: Drawable) {
    Glide.with(view.context)
        .load(url)
        .apply(
            RequestOptions.centerCropTransform()
                .placeholder(placeholder).error(errorHolder)
        ).into(view)
}

@BindingAdapter("bind:banners")
fun loadBannerImg(banner: Banner, banners: List<BannerData>?) {
    if (banners.isNullOrEmpty()) return

    val images = arrayListOf<String>()
    banners.forEach { images.add(it.imagePath) }

    banner.setImages(images)
        .setImageLoader(GlideLoader()).setDelayTime(5000).start()

    banner.setOnBannerListener { position ->
        AppUtils.launchUrl(banner.context, banners[position].url)
    }
}

class GlideLoader : ImageLoader() {
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        Glide.with(context).load(path).apply(RequestOptions.centerCropTransform()).into(imageView)
    }
}
