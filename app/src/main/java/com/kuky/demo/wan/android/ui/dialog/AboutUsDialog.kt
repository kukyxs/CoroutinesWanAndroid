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
                            mAboutUsHandler?.invoke("https://github.com/kukyxs")
                            dialog?.dismiss()
                        }
                    }, 0, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
                }

        mBinding.tSpan =
            SpannableString("Taonce：Android码农一枚，喜欢Android、Kotlin，日常撸码、撸电影、撸游戏~~")
                .apply {
                    setSpan(object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            mAboutUsHandler?.invoke("https://github.com/Taonce")
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

typealias AboutUsHandler = (String) -> Unit