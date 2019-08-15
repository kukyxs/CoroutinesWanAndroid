package com.kuky.demo.wan.android.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogLoginBinding
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainRepository
import com.kuky.demo.wan.android.ui.main.MainViewModel
import kotlinx.android.synthetic.main.dialog_login.view.*
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

    override fun getLayoutId(): Int = R.layout.dialog_login

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@LoginDialogFragment
    }

    fun login(view: View) {
        val username = mBinding.root.user_name.text.toString()
        val password = mBinding.root.password.text.toString()

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
        RegisterDialogFragment().show(requireActivity().supportFragmentManager, "register")
    }

    fun close(view: View) {
        dialog?.dismiss()
    }
}