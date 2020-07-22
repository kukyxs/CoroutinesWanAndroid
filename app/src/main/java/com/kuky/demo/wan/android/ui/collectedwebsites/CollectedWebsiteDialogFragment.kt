package com.kuky.demo.wan.android.ui.collectedwebsites

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.base.handleResult
import com.kuky.demo.wan.android.databinding.DialogCollectedWebsiteBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @author kuky.
 * @description
 */
class CollectedWebsiteDialogFragment : BaseDialogFragment<DialogCollectedWebsiteBinding>() {
    companion object {
        fun createCollectedDialog(editMode: Boolean, editId: Int = -1, editName: String = "", editLink: String = "") =
            CollectedWebsiteDialogFragment().apply {
                arguments = bundleOf(
                    "edit_mode" to editMode, "edit_id" to editId,
                    "edit_name" to editName, "edit_link" to editLink
                )
            }
    }

    private val mEditId by lazy { arguments?.getInt("edit_id") ?: -1 }

    private val mEditMode by lazy { arguments?.getBoolean("edit_mode") ?: false }

    private val mEditName by lazy { arguments?.getString("edit_name") ?: "" }

    private val mEditLink by lazy { arguments?.getString("edit_link") ?: "" }

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<CollectedWebsitesViewModel>()

    override fun layoutId() = R.layout.dialog_collected_website

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.fragment = this
        mBinding.collectedName.setText(mEditName)
        mBinding.collectedLink.setText(mEditLink)
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun ensure(view: View) {
        launch {
            if (!mEditMode) {
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

        mViewModel.addWebsites(websiteTitle, websiteLink).catch {
            mAppViewModel.dismissLoading()
            context?.toast(R.string.no_network)
        }.onStart {
            mAppViewModel.showLoading()
        }.onCompletion {
            mAppViewModel.dismissLoading()
        }.collectLatest {
            it.handleResult {
                mAppViewModel.reloadCollectWebsite.postValue(true)
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
        mViewModel.editWebsite(websiteId, websiteTitle, websiteLink).catch {
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

