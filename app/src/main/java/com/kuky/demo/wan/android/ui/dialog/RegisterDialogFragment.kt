package com.kuky.demo.wan.android.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.data.MainRepository
import com.kuky.demo.wan.android.databinding.DialogRegisterBinding
import com.kuky.demo.wan.android.ui.main.MainModelFactory
import com.kuky.demo.wan.android.ui.main.MainViewModel
import kotlinx.android.synthetic.main.dialog_register.*
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class RegisterDialogFragment : BaseDialogFragment<DialogRegisterBinding>() {

    private val mViewModel: MainViewModel by lazy {
        ViewModelProviders
            .of(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    override fun getLayoutId(): Int = R.layout.dialog_register

    override fun initFragment(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@RegisterDialogFragment
    }

    fun register(view: View) {
        val username = user_name.text.toString()
        val password = password.text.toString()
        val repass = repass.text.toString()

        if (username.isBlank() || password.isBlank() || repass.isBlank()) {
            requireContext().toast("请输入完整")
        } else {
            mViewModel.register(username, password, repass)

            // TODO("注册提示待完成")
//            mViewModel.hasLogin.observe(this, Observer<Boolean> {
//                requireContext().toast(if (it) "注册成功" else "注册出错")
//            })
            dialog?.dismiss()
        }
    }
}