package com.kuky.demo.wan.android.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.text.method.MovementMethod
import android.webkit.WebView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kuky.demo.wan.android.base.BasePagedListAdapter
import com.kuky.demo.wan.android.base.BaseRecyclerAdapter
import com.kuky.demo.wan.android.base.OnItemClickListener
import com.kuky.demo.wan.android.base.OnItemLongClickListener
import com.kuky.demo.wan.android.entity.BannerData
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.listener.OnBannerListener
import com.youth.banner.loader.ImageLoader

/**
 * @author kuky.
 * @description
 */

/**
 * 单独加载图片，没有 PlaceHolder
 */
@BindingAdapter("bind:img")
fun loadImage(view: ImageView, url: String) {
    Glide.with(view.context)
        .load(url).apply(RequestOptions.centerCropTransform())
        .into(view)
}

/**
 * 绑定图片加载
 * @param url 图片地址
 * @param placeholder 占位图
 * @param errorHolder 出错占位图
 */
@BindingAdapter(value = ["bind:imgUrl", "bind:placeHolder", "bind:error"], requireAll = false)
fun loadImageWithPlace(view: ImageView, url: String, placeholder: Drawable, errorHolder: Drawable) {
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
        .setImageLoader(GlideLoader())
        .setBannerStyle(BannerConfig.RIGHT)
        .setDelayTime(5000).start()

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
 * 绑定 paging adapter 长按事件
 * @param listener 点击事件，[OnItemLongClickListener]
 */
@BindingAdapter("bind:pageItemLongClick")
fun bindPagingItemClick(recyclerView: RecyclerView, listener: OnItemLongClickListener?) {
    val adapter = recyclerView.adapter

    if (adapter == null || adapter !is BasePagedListAdapter<*, *>) return

    adapter.setOnItemLongListener(listener)
}

/**
 * 绑定 RecyclerView 的点击事件
 * @param listener 点击事件，[OnItemClickListener]
 */
@BindingAdapter("bind:listItemClick")
fun bindRecyclerItemClick(recyclerView: RecyclerView, listener: OnItemClickListener?) {
    val adapter = recyclerView.adapter

    if (adapter == null || adapter !is BaseRecyclerAdapter<*>) return

    adapter.setOnItemListener(listener)
}

/**
 * 绑定 RecyclerView 的长按事件
 * @param listener 点击事件，[OnItemLongClickListener]
 */
@BindingAdapter("bind:listItemLongClick")
fun bindRecyclerItemLOngClick(recyclerView: RecyclerView, listener: OnItemLongClickListener?) {
    val adapter = recyclerView.adapter

    if (adapter == null || adapter !is BaseRecyclerAdapter<*>) return

    adapter.setOnItemLongListener(listener)
}

/**
 * 绑定 recyclerView 分割线
 */
@BindingAdapter("bind:divider")
fun bindRecyclerDivider(recyclerView: RecyclerView, decor: RecyclerView.ItemDecoration) {
    recyclerView.addItemDecoration(decor)
}

/**
 * recyclerView 是否固定高度
 */
@BindingAdapter("bind:hasFixedSize")
fun bindRecyclerHasFixedSize(recyclerView: RecyclerView, hasFixedSize: Boolean) {
    recyclerView.setHasFixedSize(hasFixedSize)
}

/**
 * recyclerView 滚动到指定 position，并指定偏移量
 */
@BindingAdapter(value = ["bind:scrollTo", "bind:offset"])
fun bindScrollTo(recyclerView: RecyclerView, position: Int, offset: Int) {
    recyclerView.layoutManager.let {
        when (it) {
            is LinearLayoutManager -> it.scrollToPositionWithOffset(position, offset)

            is GridLayoutManager -> it.scrollToPositionWithOffset(position, offset)

            is StaggeredGridLayoutManager -> it.scrollToPositionWithOffset(position, offset)
        }
    }
}

@BindingAdapter("bind:scrollListener")
fun bindRecyclerScrollListener(recyclerView: RecyclerView, l: RecyclerView.OnScrollListener) {
    recyclerView.addOnScrollListener(l)
}

/**
 * 绑定 SwipeRefreshLayout 颜色，刷新状态，监听事件
 */
@BindingAdapter(
    value = ["bind:refreshColor", "bind:refreshState", "bind:refreshListener"],
    requireAll = false
)
fun bindRefreshColor(
    refreshLayout: SwipeRefreshLayout,
    color: Int,
    refreshState: Boolean,
    listener: SwipeRefreshLayout.OnRefreshListener
) {
    refreshLayout.setColorSchemeResources(color)
    refreshLayout.isRefreshing = refreshState
    refreshLayout.setOnRefreshListener(listener)
}

@BindingAdapter("bind:refreshEnable")
fun bindRefreshEnable(refreshLayout: SwipeRefreshLayout, enable: Boolean) {
    refreshLayout.isEnabled = enable
}

/**
 * 绑定 ViewPager 的一些属性
 */
@BindingAdapter("bind:limitOffset")
fun bindOffscreenPageLimit(viewPager: ViewPager, limit: Int) {
    viewPager.offscreenPageLimit = limit
}

@BindingAdapter(value = ["bind:reversed", "bind:transformer"], requireAll = false)
fun bindTransformer(viewPager: ViewPager, reversed: Boolean, transformer: ViewPager.PageTransformer) {
    viewPager.setPageTransformer(reversed, transformer)
}

@BindingAdapter(value = ["bind:currentItem", "bind:smoothScroll"])
fun bindCurrentItem(viewPager: ViewPager, current: Int, smoothScroll: Boolean) {
    viewPager.setCurrentItem(current, smoothScroll)
}

/**
 * 绑定 EditText 一些属性
 */
@BindingAdapter("bind:editAction")
fun bindEditAction(editText: EditText, editorActionListener: TextView.OnEditorActionListener) {
    editText.setOnEditorActionListener(editorActionListener)
}

/**
 * 绑定 webview 的 url
 */
@BindingAdapter("bind:url")
fun bindWebUrl(webView: WebView, url: String?) {
    if (url.isNullOrBlank()) return
    webView.loadUrl(url)
}

/**
 * 绑定 TextView 的一些属性
 */
@BindingAdapter("bind:movementMethod")
fun bindMovementMethod(textView: TextView, method: MovementMethod) {
    textView.movementMethod = method
}

@Suppress("DEPRECATION")
@BindingAdapter("bind:renderHtml")
fun bindRenderHtml(textView: TextView, description: String) {
    textView.text = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M)
        Html.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
    else Html.fromHtml(description)
}
