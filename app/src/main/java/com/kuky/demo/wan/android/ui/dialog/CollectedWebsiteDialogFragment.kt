package com.kuky.demo.wan.android.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogCollectedWebsiteBinding
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesModelFactory
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesRepository
import com.kuky.demo.wan.android.ui.collectedwebsites.CollectedWebsitesViewModel
import org.jetbrains.anko.toast


/**
 * @author Taonce.
 * @description
 */
class CollectedWebsiteDialogFragment : BaseDialogFragment<DialogCollectedWebsiteBinding>() {
    var editMode = false
    private var mEditId = 0
    private var mEditName = ""
    private var mEditLink = ""

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedWebsitesModelFactory(CollectedWebsitesRepository()))
            .get(CollectedWebsitesViewModel::class.java)
    }

    override fun getLayoutId() = R.layout.dialog_collected_website

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.fragment = this

        mBinding.collectedName.setText(mEditName)
        mBinding.collectedLink.setText(mEditLink)
    }

    fun injectWebsiteData(websiteData: WebsiteData? = null) {
        mEditName = websiteData?.name ?: ""
        mEditLink = websiteData?.link ?: ""
        mEditId = websiteData?.id ?: -1
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun ensure(view: View) {
        if (!editMode) {
            mViewModel.addWebsites(mBinding.collectedName.text.toString(), mBinding.collectedLink.text.toString(), {
                mViewModel.fetchWebSitesData()
                toastAndDismiss("添加成功")
            }, { msg, dismiss -> toastAndDismiss(msg, dismiss) })
        } else {
            mViewModel.editWebsite(mEditId, mBinding.collectedName.text.toString(), mBinding.collectedLink.text.toString(),
                {
                    mViewModel.fetchWebSitesData()
                    toastAndDismiss("修改成功")
                }, { msg, isDismiss -> toastAndDismiss(msg, isDismiss) })
        }
    }

    private fun toastAndDismiss(msg: String, isDismiss: Boolean = true) {
        requireActivity().toast(msg)
        if (isDismiss) dismiss()
    }
}

