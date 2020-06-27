package com.kuky.demo.wan.android.ui.websitedetail

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Property
import android.view.View
import android.webkit.*
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.navigation.NavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.base.DoubleClickListener
import com.kuky.demo.wan.android.databinding.FragmentWesiteDetailBinding
import org.jetbrains.anko.selector
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description 文章，banner 等网页链接显示
 */
class WebsiteDetailFragment : BaseFragment<FragmentWesiteDetailBinding>() {

    private val url: String by lazy {
        arguments?.getString("url") ?: ""
    }

    private val scrollProperty: Property<WebView, Int> by lazy {
        object : Property<WebView, Int>(Int::class.java, "") {
            override fun get(`object`: WebView?): Int = `object`?.scrollY ?: 0

            override fun set(`object`: WebView?, value: Int?) {
                `object`?.scrollTo(`object`.scrollX, value ?: 0)
            }
        }
    }

    private val scrollAnim: ObjectAnimator by lazy {
        ObjectAnimator.ofInt(mBinding?.content, scrollProperty, 0).setDuration(500)
    }

    override fun getLayoutId(): Int = R.layout.fragment_wesite_detail

    @SuppressLint("SetJavaScriptEnabled")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding?.url = url

        mBinding?.content?.let {
            it.settings.apply {
                javaScriptEnabled = true
                javaScriptCanOpenWindowsAutomatically = true
                allowFileAccess = true
                layoutAlgorithm = WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
                useWideViewPort = true
                loadWithOverviewMode = true
                setSupportMultipleWindows(true)
                setGeolocationEnabled(true)
                setSupportZoom(true)
                builtInZoomControls = true
                displayZoomControls = false
                setAppCacheEnabled(true)
                domStorageEnabled = true
                cacheMode = WebSettings.LOAD_NO_CACHE
            }

            it.webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.loadUrl(url)
                    return false
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.loadUrl(request?.url.toString())
                    return false
                }
            }

            it.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(web: WebView?, newProgress: Int) {
                    super.onProgressChanged(web, newProgress)
                    if (newProgress > 85) {
                        mBinding?.loading?.isVisible = false
                        mBinding?.shareLink?.isVisible = true
                    }
                }
            }
        }

        mBinding?.gesture = DoubleClickListener {
            singleTap = {
                val shareItems = arrayListOf(
                    resources.getString(R.string.copy_link),
                    resources.getString(R.string.share_links),
                    resources.getString(R.string.open_in_browser)
                )

                requireContext().selector(items = shareItems) { _, i ->
                    when (i) {
                        0 -> (requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager)?.let {
                            it.setPrimaryClip(ClipData.newPlainText("", url))
                            requireContext().toast("复制成功")
                        }

                        1 -> startActivity(
                            Intent.createChooser(
                                Intent().apply {
                                    putExtra(Intent.EXTRA_TEXT, url)
                                    action = Intent.ACTION_SEND
                                    type = "text/plain"
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }, resources.getString(R.string.share_links)
                            )
                        )

                        2 -> startActivity(
                            Intent().apply {
                                action = Intent.ACTION_VIEW
                                data = Uri.parse(url)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        )
                    }
                }
            }
        }

        mBinding?.scrollGesture = DoubleClickListener {
            doubleTap = {
                scrollAnim.start()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (scrollAnim.isRunning) {
            scrollAnim.cancel()
        }
    }

    companion object {
        fun viewDetail(controller: NavController, @IdRes id: Int, url: String) {
            if (url.isBlank()) return
            controller.navigate(id, bundleOf("url" to url))
        }
    }
}