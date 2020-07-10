package com.kuky.demo.wan.android.ui.main

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogLoginBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * @author kuky.
 * @description
 */
class LoginDialogFragment : BaseDialogFragment<DialogLoginBinding>() {

    private val mAppViewModel by sharedViewModel<AppViewModel>()

    private val mViewModel by sharedViewModel<MainViewModel>()

    override fun layoutId(): Int = R.layout.dialog_login

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun login(view: View) {
        val username = mBinding.userName.text.toString()
        val password = mBinding.password.text.toString()

        if (username.isBlank() || password.isBlank()) {
            context?.toast(R.string.empty_input_content)
            return
        }

        launch {
            mViewModel.login(username, password).catch {
                context?.toast(R.string.no_network)
            }.onStart {
                mAppViewModel.showLoading()
            }.onCompletion {
                mAppViewModel.dismissLoading()
                dismiss()
            }.collectLatest {
                if (it.body()?.errorCode == 0) {
                    mViewModel.hasLogin.postValue(true)
                    mViewModel.saveUser(it)
                    context?.toast(R.string.login_succeed)
                } else {
                    context?.toast(R.string.login_failed)
                }
            }
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