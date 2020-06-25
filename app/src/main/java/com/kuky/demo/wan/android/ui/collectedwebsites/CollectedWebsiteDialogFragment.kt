package com.kuky.demo.wan.android.ui.collectedwebsites

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.base.handleResult
import com.kuky.demo.wan.android.databinding.DialogCollectedWebsiteBinding
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.ui.app.AppViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast


/**
 * @author kuky.
 * @description
 */
class CollectedWebsiteDialogFragment : BaseDialogFragment<DialogCollectedWebsiteBinding>() {
    var editMode = false
    private var mEditId = 0
    private var mEditName = ""
    private var mEditLink = ""

    private val mAppViewModel by lazy { getSharedViewModel(AppViewModel::class.java) }

    private val mViewModel by lazy {
        ViewModelProvider(requireActivity(), CollectedWebsitesModelFactory(CollectedWebsitesRepository()))
            .get(CollectedWebsitesViewModel::class.java)
    }

    override fun layoutId() = R.layout.dialog_collected_website

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
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
        launch {
            if (!editMode) {
                addFavouriteWebsite(
                    mBinding.collectedName.text.toString(),
                    mBinding.collectedLink.text.toString()
                )
            } else {
                editWebsiteInfo(
                    mEditId,
                    mBinding.collectedName.text.toString(),
                    mBinding.collectedLink.text.toString()
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun addFavouriteWebsite(websiteTitle: String, websiteLink: String) {
        if (websiteTitle.isBlank() || websiteLink.isBlank()) {
            context?.toast(R.string.empty_input_content)
            return
        }

        mAppViewModel.showLoading()
        mViewModel.addWebsites(websiteTitle, websiteLink)
            .catch {
                mAppViewModel.dismissLoading()
                context?.toast(R.string.no_network)
            }.collectLatest {
                mAppViewModel.dismissLoading()
                it.handleResult {
                    context?.toast(R.string.add_favourite_succeed)
                    dismiss()
                }
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun editWebsiteInfo(websiteId: Int, websiteTitle: String, websiteLink: String) {
        if (websiteTitle.isBlank() || websiteLink.isBlank()) {
            context?.toast(R.string.empty_input_content)
            return
        }

        mAppViewModel.showLoading()
        mViewModel.editWebsite(websiteId, websiteTitle, websiteLink)
            .catch {
                mAppViewModel.dismissLoading()
                context?.toast(R.string.no_network)
            }.collectLatest {
                mAppViewModel.dismissLoading()
                it.handleResult {
                    context?.toast(R.string.edit_info_succeed)
                    dismiss()
                }
            }
    }
}

