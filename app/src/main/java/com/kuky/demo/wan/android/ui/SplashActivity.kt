package com.kuky.demo.wan.android.ui

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.kuky.demo.wan.android.R
import com.kuky.demo.wan.android.base.BaseActivity
import com.kuky.demo.wan.android.databinding.ActivitySplashBinding
import kotlinx.android.synthetic.main.activity_splash.*
import yanzhikai.textpath.PathAnimatorListener

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    companion object {
        val mHandler = Handler()
    }

    override fun getLayoutId(): Int = R.layout.activity_splash

    override fun initActivity(savedInstanceState: Bundle?) {
        enjoy.let {
            it.startAnimation(0f, 1f)
            it.setDuration(2000)
            it.setAnimatorListener(object : PathAnimatorListener() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)

                    mHandler.postDelayed({
                        startActivity(
                            Intent(this@SplashActivity, MainActivity::class.java)
                        )
                        finish()
                    }, 500)
                }
            })
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeMessages(0)
    }
}
