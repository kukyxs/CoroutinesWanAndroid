package com.kuky.demo.wan.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BannerData
import com.kuky.demo.wan.android.entity.CoinsData

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
            banners.value = repository.getHomeBanners()
        }
    }

    fun getCoins() {
        viewModelScope.safeLaunch({
            coins.value = null
        }, { coins.value = repository.getCoins() })
    }

    fun login(username: String, password: String, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch({
            fail("登录过程出错啦~请检查网络")
        }, {
            val result = repository.login(username, password)

            if (result.code == CODE_SUCCEED) {
                success()
                hasLogin.value = true
            } else {
                fail(result.message)
                hasLogin.value = false
            }
        })
    }

    fun register(
        username: String, password: String, repass: String,
        success: () -> Unit, fail: (String) -> Unit
    ) {
        viewModelScope.safeLaunch({
            fail("注册过程出错啦~请检查网络")
        }, {
            val result = repository.register(username, password, repass)
            if (result.code == CODE_SUCCEED) {
                success()
                hasLogin.value = true
            } else {
                fail(result.message)
                hasLogin.value = false
            }
        })
    }

    fun loginout(fail: (String) -> Unit) {
        viewModelScope.safeLaunch({
            fail("退出过程出错啦~请检查网络")
        }, {
            hasLogin.value = !repository.loginout()
        })
    }
}