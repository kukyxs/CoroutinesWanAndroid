package com.kuky.demo.wan.android.ui.main

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BannerData
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class MainRepository {

    fun getCachedBanners(): MutableList<BannerData>? = Gson().fromJson(
        PreferencesHelper.fetchBannerCache(WanApplication.instance),
        object : TypeToken<MutableList<BannerData>>() {}.type
    )

    suspend fun getCoins() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.fetchUserCoins(cookie).data
        }

    suspend fun getHomeBanners() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.homeBanner().data
        }

    suspend fun login(username: String, password: String) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.login(username, password)
        }

    suspend fun register(username: String, password: String, repass: String) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.register(username, password, repass)
        }

    suspend fun loginOut() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.loginOut()
        }
}