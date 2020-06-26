package com.kuky.demo.wan.android.ui.main

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
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
class AboutUsDialogFragment : BaseDialogFragment<DialogAboutUsBinding>() {

    var aboutUsHandler: ((String) -> Unit)? = null

    override fun layoutId(): Int = R.layout.dialog_about_us

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@AboutUsDialogFragment
        mBinding.movementMethod = LinkMovementMethod.getInstance()

        mBinding.kSpan = SpannableStringBuilder("Kukyxs").apply {
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    aboutUsHandler?.invoke("https://github.com/kukyxs")
                    dialog?.dismiss()
                }
            }, 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

            append(": 逗比码农一枚, 主要做 Android, 会写点 Flutter")
        }

        mBinding.tSpan = SpannableStringBuilder("Taonce").apply {
            setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    aboutUsHandler?.invoke("https://github.com/Taonce")
                    dialog?.dismiss()
                }
            }, 0, length, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)

            append(": Android码农一枚, 喜欢Android、Kotlin, 日常撸码、撸电影、撸游戏~~")
        }
    }

    fun ensure(view: View) {
        dialog?.dismiss()
    }
}