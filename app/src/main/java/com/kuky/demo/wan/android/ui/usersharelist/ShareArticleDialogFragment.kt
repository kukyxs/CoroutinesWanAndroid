package com.kuky.demo.wan.android.ui.usersharelist

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogShareArticleBinding
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class ShareArticleDialogFragment : BaseDialogFragment<DialogShareArticleBinding>() {

    private val mViewModel: UserShareListViewModel by lazy {
        ViewModelProvider(requireActivity(), UserShareListModelFactory(UserShareListRepository()))
            .get(UserShareListViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.dialog_share_article

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.fragment = this@ShareArticleDialogFragment
    }

    fun cancel(view: View) {
        dismiss()
    }

    fun ensure(view: View) {
        mViewModel.putAShare(mBinding.sharedName.text.toString(), mBinding.sharedLink.text.toString(), {
            requireContext().toast("添加成功")
            dismiss()
        }, { msg ->
            requireContext().toast(msg)
        })
    }
}