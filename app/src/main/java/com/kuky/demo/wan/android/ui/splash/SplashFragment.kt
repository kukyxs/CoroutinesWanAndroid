package com.kuky.demo.wan.android.ui.splash

import android.os.Bundle
import android.view.View
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseFragment
import com.kuky.demo.wan.android.databinding.FragmentSplashBinding

/**
 * @author kuky.
 * @description
 */
class SplashFragment : BaseFragment<FragmentSplashBinding>() {

    override fun getLayoutId(): Int = R.layout.fragment_splash

    override fun initFragment(view: View, savedInstanceState: Bundle?) {

        view.postDelayed({
            mNavController.let {
                val graph = it.navInflater.inflate(R.navigation.wan_navigation)
                    .apply { startDestination = R.id.mainFragment }

                it.graph = graph
            }
        }, 3000)
    }
}