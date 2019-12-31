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
 * @description
 */
abstract class BaseFragment<VB : ViewDataBinding> : Fragment(), CoroutineScope by MainScope() {

    protected var mBinding: VB? = null
    protected lateinit var mNavController: NavController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true

        if (mBinding == null) {
            mBinding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
            actionsOnViewInflate()
        }

        mNavController = NavHostFragment.findNavController(this)

        return if (mBinding != null) {
            mBinding!!.root.apply { (parent as? ViewGroup)?.removeView(this) }
        } else super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding?.lifecycleOwner = this
        initFragment(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        mBinding?.unbind()
    }

    /**
     * 该方法完整走完一个生命周期只会走一次，可用于该页面进入时网络请求
     */
    open fun actionsOnViewInflate() {}

    abstract fun getLayoutId(): Int

    abstract fun initFragment(view: View, savedInstanceState: Bundle?)

    fun <T : ViewModel> getViewModel(clazz: Class<T>): T =
        ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(clazz)

    fun <T : ViewModel> getSharedViewModel(clazz: Class<T>): T =
        ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory()).get(clazz)

    /**
     * 权限申请，依赖的 activity 需继承 [BaseActivity]
     */
    fun onRuntimePermissionRequest(
        permissions: Array<String>, listener: PermissionListener
    ) = if (activity != null && activity is BaseActivity<*>) {
        (activity as BaseActivity<*>).onRuntimePermissionsAsk(permissions, listener)
    } else {
        throw RuntimeException("Attached activity is not [com.kuky.demo.wan.android.base.BaseActivity], check it")
    }
}