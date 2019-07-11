package com.kuky.demo.wan.android.data

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * @author kuky.
 * @description
 */
class MainRepository {

    suspend fun getHomeBanners() = withContext(Dispatchers.IO) {
        val banners = RetrofitManager.apiService.homeBanner().data
        // need cache
        banners
    }

    suspend fun login(username: String, password: String) = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.login(username, password)

        if (result.errorCode == 0) {
            PreferencesHelper.saveUserId(WanApplication.instance, result.data.id)
            PreferencesHelper.saveUserName(WanApplication.instance, result.data.nickname)
            true
        } else false
    }

    suspend fun register(username: String, password: String, repass: String) = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.register(username, password, repass)

        if (result.errorCode == 0) {
            PreferencesHelper.saveUserId(WanApplication.instance, result.data.id)
            PreferencesHelper.saveUserName(WanApplication.instance, result.data.nickname)
            true
        } else false
    }

    suspend fun loginout() = withContext(Dispatchers.IO) {
        val response = RetrofitManager.apiService.loginout().string()
        val errorCode = JSONObject(response).optInt("errorCode")

        if (errorCode == 0) {
            PreferencesHelper.saveUserId(WanApplication.instance, 0)
            PreferencesHelper.saveUserName(WanApplication.instance, "")
            true
        } else false
    }
}