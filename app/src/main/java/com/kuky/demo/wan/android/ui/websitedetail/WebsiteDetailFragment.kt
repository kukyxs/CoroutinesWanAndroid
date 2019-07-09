package com.kuky.demo.wan.android.ui.websitedetail

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.IdRes
import androidx.navigation.NavController
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentWesiteDetailBinding
import kotlinx.android.synthetic.main.fragment_wesite_detail.*

/**
 * @author kuky.
 * @description 文章，banner 等网页链接显示
 */
class WebsiteDetailFragment : BaseFragment<FragmentWesiteDetailBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_wesite_detail

    @SuppressLint("SetJavaScriptEnabled")
    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        mBinding.url = arguments?.getString("url")

        content.settings.apply {
            javaScriptEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
        }

        content.apply {
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                    view?.let { loadUrl(url) }
                    return true
                }

                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    view?.let { loadUrl(request?.url.toString()) }
                    return true
                }
            }
        }
    }

    companion object {
        fun viewDetail(controller: NavController, @IdRes id: Int, url: String) {
            if (url.isBlank()) return
            controller.navigate(id, Bundle().apply { putString("url", url) })
        }
    }
}