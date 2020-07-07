package com.kuky.demo.wan.android.ui.usersharelist

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogShareArticleBinding
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
class ShareArticleDialogFragment : BaseDialogFragment<DialogShareArticleBinding>() {

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by viewModel<UserShareListViewModel>()

    override fun layoutId(): Int = R.layout.dialog_share_article

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.fragment = this@ShareArticleDialogFragment
    }

    fun cancel(view: View) {
        dismiss()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun ensure(view: View) {
        val title = mBinding.sharedName.text.toString()
        val link = mBinding.sharedLink.text.toString()

        if (title.isBlank() || link.isBlank()) {
            context?.toast(R.string.empty_input_content)
            return
        }

        launch {
            mViewModel.putAShare(title, link).catch {
                context?.toast(R.string.no_network)
            }.onStart {
                mAppViewModel.showLoading()
            }.onCompletion {
                mAppViewModel.dismissLoading()
            }.collectLatest {
                context?.toast(R.string.add_succeed)
                dismiss()
            }
        }
    }
}