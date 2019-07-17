package com.kuky.demo.wan.android.ui.dialog

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogCollectedWebsiteBinding
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesFactory
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesRepository
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesViewModel
import com.kuky.demo.wan.android.utils.ScreenUtils
import kotlinx.android.synthetic.main.dialog_collected_website.*
import org.jetbrains.anko.toast


/**
 * @author Taonce.
 * @description
 */
class CollectedWebsiteDialogFragment : BaseDialogFragment<DialogCollectedWebsiteBinding>() {
    private val viewModel by lazy {
        ViewModelProviders.of(requireActivity(), CollectedWebsitesFactory(CollectedWebsitesRepository()))
            .get(CollectedWebsitesViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        val attrs = dialog?.window?.attributes?.apply {
            width = (ScreenUtils.getScreenWidth(requireContext()) * 0.8f).toInt()
            gravity = Gravity.CENTER
        }

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(0))
            attributes = attrs
        }
    }

    override fun getLayoutId() = R.layout.dialog_collected_website

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.fragment = this
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun ensure(view: View) {
        viewModel.addWebsites(collected_name.text.toString(), collected_link.text.toString(), {
            viewModel.fetchWebSitesData()
            toastAndDismiss("添加成功")
        }, { toastAndDismiss(it) })
    }

    private fun toastAndDismiss(msg: String) {
        requireActivity().toast(msg)
        dismiss()
    }
}

