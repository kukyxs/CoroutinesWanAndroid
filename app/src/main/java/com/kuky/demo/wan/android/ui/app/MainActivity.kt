package com.kuky.demo.wan.android.ui.app

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseActivity
import com.kuky.demo.wan.android.base.UiState
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.ActivityMainBinding
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.utils.getAppVersionName
import kotlinx.coroutines.flow.collect
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.scope.Scope

class MainActivity : BaseActivity<ActivityMainBinding>(), AndroidScopeComponent {

    override val scope: Scope by activityScope()

    private val mAppViewModel by viewModel<AppViewModel>()

    private val mLoadingDialog by inject<LoadingDialog>()

    private val mConnectivityManager by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val mNetworkRequest by lazy {
        NetworkRequest.Builder().build()
    }

    private val mNetStateCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                mAvailableTypeCount++
                checkState()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                mAvailableTypeCount--
                checkState()
            }
        }
    }

    private var mAvailableTypeCount = 0

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {
        mConnectivityManager.registerNetworkCallback(mNetworkRequest, mNetStateCallback)

        if (PreferencesHelper.isFirstIn(this)) {
            alert(
                String.format(
                    resources.getString(R.string.operate_helper),
                    getAppVersionName()
                ), resources.getString(R.string.operate_title)
            ) {
                isCancelable = false
                yesButton { PreferencesHelper.saveFirstState(this@MainActivity, false) }
            }.show()
        }

        lifecycleScope.launchWhenCreated {
            mAppViewModel.wholeState.collect {
                when (it) {
                    UiState.Loading -> if (mLoadingDialog.isHidden) {
                        mLoadingDialog.showAllowStateLoss(supportFragmentManager, "loading")
                    }

                    else -> if (mLoadingDialog.isAdded) {
                        mLoadingDialog.dismissAllowingStateLoss()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mConnectivityManager.unregisterNetworkCallback(mNetStateCallback)
    }

    private fun checkState() {
        mBinding.netAvailable = mAvailableTypeCount > 0
    }

    override fun onBackPressed() {
        supportFragmentManager.fragments.first()
            .childFragmentManager.fragments.last().let {
                if (it is MainFragment) {
                    startActivity(Intent(Intent.ACTION_MAIN).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        addCategory(Intent.CATEGORY_HOME)
                    });return
                }
            }

        super.onBackPressed()
    }
}
