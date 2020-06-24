package com.kuky.demo.wan.android.ui.app

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogLoadingBinding

/**
 * @author kuky.
 * @description
 */
class LoadingDialog : BaseDialogFragment<DialogLoadingBinding>() {

    override fun layoutId() = R.layout.dialog_loading

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        (mBinding.loading.drawable as? AnimationDrawable)?.start()
    }

    override fun dialogFragmentAnim() = R.style.DialogAlterWithNoAnimation

    override fun dialogFragmentAttributes() = dialog?.window?.attributes?.apply {
        width = WindowManager.LayoutParams.WRAP_CONTENT
        height = WindowManager.LayoutParams.WRAP_CONTENT
        gravity = Gravity.CENTER
    }
}