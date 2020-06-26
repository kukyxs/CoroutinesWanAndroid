package com.kuky.demo.wan.android.ui.main

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseDialogFragment
import com.kuky.demo.wan.android.databinding.DialogRegisterBinding
import com.kuky.demo.wan.android.ui.app.AppViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import org.jetbrains.anko.toast

/**
 * @author kuky.
 * @description
 */
class RegisterDialogFragment : BaseDialogFragment<DialogRegisterBinding>() {

    private val mAppViewModel by lazy { getSharedViewModel(AppViewModel::class.java) }

    private val mViewModel: MainViewModel by lazy {
        ViewModelProvider(requireActivity(), MainModelFactory(MainRepository()))
            .get(MainViewModel::class.java)
    }

    override fun layoutId(): Int = R.layout.dialog_register

    override fun initDialog(view: View, savedInstanceState: Bundle?) {
        mBinding.holder = this@RegisterDialogFragment
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun register(view: View) {
        val username = mBinding.userName.text.toString()
        val password = mBinding.password.text.toString()
        val repass = mBinding.repass.text.toString()

        if (username.isBlank() || password.isBlank() || repass.isBlank()) {
            context?.toast(R.string.empty_input_content)
            return
        }

        launch {
            mViewModel.register(username, password, repass).catch {
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
                    context?.toast(R.string.register_failed)
                }
            }
        }
    }

    fun close(view: View) {
        dialog?.dismiss()
    }
}