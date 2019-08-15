package com.kuky.demo.wan.android.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogCollectedWebsiteBinding
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesFactory
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesRepository
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesViewModel
import kotlinx.android.synthetic.main.dialog_collected_website.*
import org.jetbrains.anko.toast


/**
 * @author Taonce.
 * @description
 */
class CollectedWebsiteDialogFragment : BaseDialogFragment<DialogCollectedWebsiteBinding>() {
    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedWebsitesFactory(CollectedWebsitesRepository()))
            .get(CollectedWebsitesViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setWindowAnimations(R.style.DialogPushInOutAnimation)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getLayoutId() = R.layout.dialog_collected_website

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.fragment = this
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun ensure(view: View) {
        mViewModel.addWebsites(collected_name.text.toString(), collected_link.text.toString(), {
            mViewModel.fetchWebSitesData()
            toastAndDismiss("添加成功")
        }, { msg, dismiss -> toastAndDismiss(msg, dismiss) })
    }

    private fun toastAndDismiss(msg: String, isDismiss: Boolean = true) {
        requireActivity().toast(msg)
        if (isDismiss) dismiss()
    }
}

