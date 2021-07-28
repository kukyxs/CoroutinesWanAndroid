package com.kuky.demo.wan.android.ui.main

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.BaseResultData
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BannerData
import com.kuky.demo.wan.android.entity.WanUserData
import com.kuky.demo.wan.android.extension.safeLaunch
import kotlinx.coroutines.flow.flow
import retrofit2.Response

/**
 * @author kuky.
 * @description
 */
class MainViewModel(
    private val repository: MainRepository
) : ViewModel() {

    val hasLogin = MutableLiveData<Boolean>()
    val banners = MutableLiveData<List<BannerData>>()

    init {
        hasLogin.value = PreferencesHelper.hasLogin(WanApplication.instance)
        banners.value = repository.getCachedBanners()
    }

    fun getBanners() {
        viewModelScope.safeLaunch {
            block = {
                banners.value = repository.getHomeBanners().apply {
                    PreferencesHelper.saveBannerCache(WanApplication.instance, Gson().toJson(this))
                }
            }
        }
    }

    fun getCoinInfo() = flow {
        emit(repository.getCoins())
    }

    fun login(username: String, password: String) = flow {
        emit(repository.login(username, password))
    }

    fun register(username: String, password: String, repass: String) = flow {
        emit(repository.register(username, password, repass))
    }

    fun loginOut() = flow {
        emit(repository.loginOut())
    }

    fun clearUserInfo() {
        PreferencesHelper.saveUserId(WanApplication.instance, 0)
        PreferencesHelper.saveUserName(WanApplication.instance, "")
        PreferencesHelper.saveCookie(WanApplication.instance, "")
    }

    // 存储用户信息
    fun saveUser(info: Response<BaseResultData<WanUserData>>) {
        if (info.body()?.errorCode == 0) {
            val cookies = StringBuilder()

            info.headers()
                .filter { TextUtils.equals(it.first, "Set-Cookie") }
                .forEach { cookies.append(it.second).append(";") }

            val strCookie =
                if (cookies.endsWith(";")) cookies.substring(0, cookies.length - 1)
                else cookies.toString()

            PreferencesHelper.saveCookie(WanApplication.instance, strCookie)
            PreferencesHelper.saveUserId(WanApplication.instance, info.body()?.data?.id ?: 0)
            PreferencesHelper.saveUserName(WanApplication.instance, info.body()?.data?.nickname ?: "")
        }
    }
}