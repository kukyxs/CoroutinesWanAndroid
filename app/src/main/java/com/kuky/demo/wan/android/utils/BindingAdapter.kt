package com.kuky.demo.wan.android.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.webkit.WebView
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.entity.BannerData
import com.youth.banner.Banner
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.loader.ImageLoader

/**
 * @author kuky.
 * @description
 */

/**
 * 绑定图片加载
 * @param url 图片地址
 * @param placeholder 占位图
 * @param errorHolder 出错占位图
 */
@BindingAdapter(value = ["bind:imgUrl", "bind:placeHolder", "bind:error"], requireAll = false)
fun loadImage(view: ImageView, url: String, placeholder: Drawable, errorHolder: Drawable) {
    Glide.with(view.context)
        .load(url)
        .apply(
            RequestOptions.centerCropTransform()
                .placeholder(placeholder).error(errorHolder)
        ).into(view)
}

/**
 * 绑定本地圆形头像
 */
@BindingAdapter("bind:circleImg")
fun bindCircleImage(imageView: ImageView, imgRes: Drawable) {
    Glide.with(imageView.context)
        .load(imgRes)
        .apply(RequestOptions.bitmapTransform(RoundedCorners(360)))
        .into(imageView)
}

/**
 * 绑定 Banner 图片列表和点击事件
 * @param banners Banner 信息列表
 * @param listener 点击事件
 */
@BindingAdapter(value = ["bind:banners", "bind:bannerClick"], requireAll = false)
fun loadBannerImg(banner: Banner, banners: List<BannerData>?, listener: OnBannerListener) {
    if (banners.isNullOrEmpty()) return

    val images = arrayListOf<String>()
    banners.forEach { images.add(it.imagePath) }

    banner.setImages(images)
        .setImageLoader(GlideLoader()).setDelayTime(5000).start()

    banner.setOnBannerListener(listener)
}

class GlideLoader : ImageLoader() {
    override fun displayImage(context: Context, path: Any, imageView: ImageView) {
        Glide.with(context).load(path).apply(RequestOptions.centerCropTransform()).into(imageView)
    }
}

/**
 * 绑定 paging adapter 点击事件
 * @param listener 点击事件，[OnItemClickListener]
 */
@BindingAdapter("bind:pageItemClick")
fun bindPagingItemClick(recyclerView: RecyclerView, listener: OnItemClickListener?) {
    val adapter = recyclerView.adapter

    if (adapter == null || adapter !is BasePagedListAdapter<*, *>) return

    adapter.setOnItemListener(listener)
}

/**
 * 绑定 webview 的 url
 * @param url
 */
@BindingAdapter("bind:url")
fun bindWebUrl(webView: WebView, url: String?) {
    if (url.isNullOrBlank()) return
    webView.loadUrl(url)
}

/**
 * 绑定 RecyclerView 的点击事件
 * @param listener 点击事件，[OnItemClickListener]
 */
@BindingAdapter("bind:listItemClick")
fun bindRecyclerItemClick(recyclerView: RecyclerView, listener: OnItemClickListener?) {
    val adapter = recyclerView.adapter

    if (adapter == null || adapter !is BaseRecyclerAdapter<*, *>) return

    adapter.setOnItemListener(listener)
}

/**
 * 绑定分割线
 */
@BindingAdapter("bind:divider")
fun bindRecyclerDivider(recyclerView: RecyclerView, decor: RecyclerView.ItemDecoration) {
    recyclerView.addItemDecoration(decor)
}
