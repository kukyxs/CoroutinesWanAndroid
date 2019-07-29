package com.kuky.demo.wan.android.ui.main

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.CODE_FAILED
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.ResultBack
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BannerData
import com.kuky.demo.wan.android.entity.WanUserEntity
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @author kuky.
 * @description
 */
class MainRepository {

    fun getCachedBanners(): List<BannerData>? = Gson().fromJson(
        PreferencesHelper.fetchBannerCache(WanApplication.instance),
        object : TypeToken<List<BannerData>>() {}.type
    )

    suspend fun getHomeBanners() = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.homeBanner().data
        PreferencesHelper.saveBannerCache(WanApplication.instance, Gson().toJson(result))
        result
    }

    suspend fun login(username: String, password: String) = withContext(Dispatchers.IO) {
        val response = RetrofitManager.apiService.login(username, password)
        handleUserResponse(response)
    }

    suspend fun register(username: String, password: String, repass: String) = withContext(Dispatchers.IO) {
        val response = RetrofitManager.apiService.register(username, password, repass)
        handleUserResponse(response)
    }

    private suspend fun handleUserResponse(response: Response<WanUserEntity>): ResultBack {
        val result = response.body()

        return suspendCoroutine { continuation ->
            if (result == null) continuation.resumeWithException(RuntimeException("null response"))

            if (result!!.errorCode == 0) {
                val cookies = StringBuilder()

                response.headers()
                    .filter { TextUtils.equals(it.first, "Set-Cookie") }
                    .forEach { cookies.append(it.second).append(";") }

                val strCookie =
                    if (cookies.endsWith(";")) cookies.substring(0, cookies.length - 1)
                    else cookies.toString()

                PreferencesHelper.saveCookie(WanApplication.instance, strCookie)
                PreferencesHelper.saveUserId(WanApplication.instance, result.data.id)
                PreferencesHelper.saveUserName(
                    WanApplication.instance, result.data.nickname
                )
                continuation.resume(ResultBack(CODE_SUCCEED, ""))
            } else continuation.resume(ResultBack(CODE_FAILED, result.errorMsg))
        }
    }

    suspend fun loginout() = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.loginout()

        if (result.errorCode == 0) {
            PreferencesHelper.saveUserId(WanApplication.instance, 0)
            PreferencesHelper.saveUserName(WanApplication.instance, "")
            PreferencesHelper.saveCookie(WanApplication.instance, "")
            true
        } else false
    }
}