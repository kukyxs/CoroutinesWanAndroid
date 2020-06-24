package com.kuky.demo.wan.android.ui.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogLoginBinding
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class LoginDialogFragment : BaseDialogFragment<DialogLoginBinding>() {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.dialog_login

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@LoginDialogFragment
    }

    fun login(view: View) {
        val username = mBinding.userName.text.toString()
        val password = mBinding.password.text.toString()

        if (username.isBlank() || password.isBlank()) {
            requireContext().toast("请输入完整")
        } else {
            mViewModel.login(username, password, {
                requireContext().toast("登录成功")
                dialog?.dismiss()
            }, { message ->
                requireContext().toast(message)
                dialog?.dismiss()
            })
        }
    }

    fun register(view: View) {
        dialog?.dismiss()
        RegisterDialogFragment().showAllowStateLoss(requireActivity().supportFragmentManager, "register")
    }

    fun close(view: View) {
        dialog?.dismiss()
    }
}