package com.kuky.demo.wan.android.ui

import android.content.Intent
import android.os.Bundle
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseActivity
import com.kuky.demo.wan.android.databinding.ActivityMainBinding
import com.kuky.demo.wan.android.ui.main.MainFragment

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {

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
