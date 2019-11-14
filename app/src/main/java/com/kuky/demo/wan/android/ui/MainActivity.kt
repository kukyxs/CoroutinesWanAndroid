package com.kuky.demo.wan.android.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Bundle
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseActivity
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.databinding.ActivityMainBinding
import com.kuky.demo.wan.android.ui.main.MainFragment
import com.kuky.demo.wan.android.utils.ApplicationUtils
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class MainActivity : BaseActivity<ActivityMainBinding>() {
    private var availableCount = 0

    private val manager: ConnectivityManager by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val request: NetworkRequest by lazy {
        NetworkRequest.Builder().build()
    }

    private val netStateCallback: ConnectivityManager.NetworkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                availableCount++
                checkState()
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                availableCount--
                checkState()
            }
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {
        manager.registerNetworkCallback(request, netStateCallback)

        if (PreferencesHelper.isFirstIn(this)) {
            alert(
                String.format(
                    resources.getString(R.string.operate_helper),
                    ApplicationUtils.getAppVersionName(this)
                ), resources.getString(R.string.operate_title)
            ) {
                isCancelable = false
                yesButton { PreferencesHelper.saveFirstState(this@MainActivity, false) }
            }.show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        manager.unregisterNetworkCallback(netStateCallback)
    }

    private fun checkState() {
        mBinding.netAvailable = availableCount > 0
    }

    override fun needTransparentStatus(): Boolean = true

    override fun onBackPressed() {
        supportFragmentManager.fragments.first()
            .childFragmentManager.fragments.last().let {
            if (it is MainFragment) {
                startActivity(Intent(Intent.ACTION_MAIN)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        addCategory(Intent.CATEGORY_HOME)
                    })
                return
            }
        }

        super.onBackPressed()
    }
}
