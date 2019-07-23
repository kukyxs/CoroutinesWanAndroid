package com.kuky.demo.wan.android.ui.dialog

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogAboutUsBinding

/**
 * @author kuky.
 * @description
 */
class AboutUsDialog : BaseDialogFragment<DialogAboutUsBinding>() {

    private var mAboutUsHandler: AboutUsHandler? = null

    override fun getLayoutId(): Int = R.layout.dialog_about_us

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@AboutUsDialog
        mBinding.movementMethod = LinkMovementMethod.getInstance()
        mBinding.kSpan =
            SpannableString("Kukyxs：逗比码农一枚，主要做 Android，会写点 Flutter，Python，小程序和 Django 后台，传说中的样样通样样松~~")
                .apply {
                    setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            mAboutUsHandler?.spanClick("https://github.com/kukyxs")
                            dialog?.dismiss()
                        }
                    }, 0, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }

        mBinding.tSpan =
            SpannableString("Taonce：微信公众号 Taonce 博主，专注于 Kotlin 和 Android 方面的知识，并且保持高频率、高质量的文章供大家一起学习交流。")
                .apply {
                    setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            mAboutUsHandler?.spanClick("https://github.com/Taonce")
                            dialog?.dismiss()
                        }
                    }, 0, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }
    }

    fun setHandler(handler: AboutUsHandler?): AboutUsDialog {
        this.mAboutUsHandler = handler
        return this@AboutUsDialog
    }

    fun ensure(view: View) {
        dialog?.dismiss()
    }
}

interface AboutUsHandler {
    fun spanClick(url: String)
}