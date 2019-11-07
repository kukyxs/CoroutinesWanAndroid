package com.kuky.demo.wan.android.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseActivity
import com.kuky.demo.wan.android.databinding.ActivitySplashBinding

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    companion object {
        val mHandler = Handler()
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initActivity(savedInstanceState: Bundle?) {
        mHandler.postDelayed({
            startActivity(
                Intent(this@SplashActivity, MainActivity::class.java)
            )
            finish()
        }, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeMessages(0)
    }
}
