package com.kuky.demo.wan.android.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

/**
 * @author kuky.
 * @description Fragment 基类
 */
abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), CoroutineScope by MainScope() {
    protected lateinit var mBinding: VB
    protected lateinit var mNavController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        mNavController = NavHostFragment.findNavController(this)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.lifecycleOwner = this
        initFragment(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        mBinding.unbind()
    }

    abstract fun getLayoutId(): Int

    abstract fun initFragment(view: View, savedInstanceState: Bundle?)

    fun <T : ViewModel> getViewModel(clazz: Class<T>): T = ViewModelProvider(this).get(clazz)

    fun <T : ViewModel> getSharedViewModel(clazz: Class<T>): T = ViewModelProvider(requireActivity()).get(clazz)

    /**
     * 权限申请，依赖的 activity 需继承 [BaseActivity]
     */
    fun onRuntimePermissionRequest(
        permissions: Array<String>, listener: PermissionListener
    ) = if (activity != null && activity is BaseActivity<*>) {
        (activity as BaseActivity<*>).onRuntimePermissionsAsk(permissions, listener)
    } else {
        throw RuntimeException("Binned activity is not [BaseActivity], and check it")
    }
}