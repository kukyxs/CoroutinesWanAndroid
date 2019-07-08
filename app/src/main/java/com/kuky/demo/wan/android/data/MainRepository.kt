package com.kuky.demo.wan.android.data

import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class MainRepository  {

    suspend fun getHomeBanners() = withContext(Dispatchers.IO) {
        val banners = RetrofitManager.apiService.homeBanner().data
        // need cache
        banners
    }
}