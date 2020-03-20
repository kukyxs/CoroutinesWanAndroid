package com.kuky.demo.wan.android.ui.main

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BannerData
import com.kuky.demo.wan.android.entity.CoinsData
import com.kuky.demo.wan.android.entity.WanUserEntity
import retrofit2.Response

/**
 * @author kuky.
 * @description
 */
class MainViewModel(private val repository: MainRepository) : ViewModel() {
    val hasLogin = MutableLiveData<Boolean>()
    val banners = MutableLiveData<List<BannerData>>()
    val coins = MutableLiveData<CoinsData?>()

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

    fun getCoins() {
        viewModelScope.safeLaunch {
            block = { coins.value = repository.getCoins() }
        }
    }

    fun login(username: String, password: String, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            block = {
                repository.login(username, password).let {
                    if (it.body()?.errorCode == 0) {
                        saveUser(it)
                        success()
                        hasLogin.value = true
                    } else {
                        fail(it.body()?.errorMsg ?: "登录失败~")
                        hasLogin.value = false
                    }
                }
            }
            onError = {
                fail("登录过程出错啦~请检查网络")
            }
        }
    }

    fun register(
        username: String, password: String, repass: String,
        success: () -> Unit, fail: (String) -> Unit
    ) {
        viewModelScope.safeLaunch {
            block = {
                repository.register(username, password, repass).let {
                    if (it.body()?.errorCode == 0) {
                        saveUser(it)
                        success()
                        hasLogin.value = true
                    } else {
                        fail(it.body()?.errorMsg ?: "注册失败~")
                        hasLogin.value = false
                    }
                }
            }
            onError = {
                fail("注册过程出错啦~请检查网络")
            }
        }
    }

    fun loginOut(fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            block = {
                repository.loginOut().let {
                    if (it.errorCode == 0) {
                        hasLogin.value = false
                        PreferencesHelper.saveUserId(WanApplication.instance, 0)
                        PreferencesHelper.saveUserName(WanApplication.instance, "")
                        PreferencesHelper.saveCookie(WanApplication.instance, "")
                    } else {
                        hasLogin.value = true
                        fail("退出账号失败~")
                    }
                }
            }
            onError = {
                fail("退出过程出错啦~请检查网络")
            }
        }
    }

    // 存储用户信息
    private fun saveUser(info: Response<WanUserEntity>) {
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