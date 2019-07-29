package com.kuky.demo.wan.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BannerData

/**
 * @author kuky.
 * @description
 */
class MainViewModel(private val repository: MainRepository) : ViewModel() {
    val hasLogin = MutableLiveData<Boolean>()
    val banners = MutableLiveData<List<BannerData>>()

    init {
        hasLogin.value = PreferencesHelper.hasLogin(WanApplication.instance)
        banners.value = repository.getCachedBanners()
    }

    fun getBanners() {
        viewModelScope.safeLaunch {
            banners.value = repository.getHomeBanners()
        }
    }

    // 未找到较好的解决方案，目前使用回调进行处理，有较好的方案请提 issue
    fun login(username: String, password: String, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            val result = repository.login(username, password)

            if (result.code == CODE_SUCCEED) {
                success()
                hasLogin.value = true
            } else {
                fail(result.message)
                hasLogin.value = false
            }
        }
    }

    fun register(
        username: String, password: String, repass: String,
        success: () -> Unit, fail: (String) -> Unit
    ) {
        viewModelScope.safeLaunch {
            val result = repository.register(username, password, repass)
            if (result.code == CODE_SUCCEED) {
                success()
                hasLogin.value = true
            } else {
                fail(result.message)
                hasLogin.value = false
            }
        }
    }

    fun loginout() {
        viewModelScope.safeLaunch {
            hasLogin.value = !repository.loginout()
        }
    }
}