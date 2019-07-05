package com.kuky.demo.wan.android

import android.os.Bundle
import com.kuky.demo.wan.android.base.BaseActivity
import com.kuky.demo.wan.android.databinding.ActivityMainBinding
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : BaseActivity<ActivityMainBinding>() {

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initActivity(savedInstanceState: Bundle?) {

        launch(Dispatchers.IO) {
            val data = RetrofitManager.apiService.homeArticles(0)

            withContext(Dispatchers.Main) {
                LogUtils.info(data.data.curPage)
            }
        }
    }
}
