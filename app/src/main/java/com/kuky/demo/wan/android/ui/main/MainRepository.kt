package com.kuky.demo.wan.android.ui.main

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BannerData
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class MainRepository(private val api: ApiService) {

    fun getCachedBanners(): MutableList<BannerData>? = Gson().fromJson(
        PreferencesHelper.fetchBannerCache(WanApplication.instance),
        object : TypeToken<MutableList<BannerData>>() {}.type
    )

    suspend fun getCoins() =
        withContext(Dispatchers.IO) {
            api.fetchUserCoins(
                PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data
        }

    suspend fun getHomeBanners() =
        withContext(Dispatchers.IO) {
            api.homeBanner().data
        }

    suspend fun login(username: String, password: String) =
        withContext(Dispatchers.IO) {
            api.login(username, password)
        }

    suspend fun register(username: String, password: String, repass: String) =
        withContext(Dispatchers.IO) {
            api.register(username, password, repass)
        }

    suspend fun loginOut() =
        withContext(Dispatchers.IO) {
            api.loginOut()
        }
}