package com.kuky.demo.wan.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.BannerData

/**
 * @author kuky.
 * @description
 */
class MainViewModel(private val repository: MainRepository) : ViewModel() {
    val hasLogin = MutableLiveData<Boolean>()
    val banners = MutableLiveData<List<BannerData>>()

    fun getBanners() {
        viewModelScope.safeLaunch {
            banners.value = repository.getHomeBanners()
        }
    }

    fun login(username: String, password: String, success: () -> Unit, fail: (message: String) -> Unit) {
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
        success: () -> Unit, fail: (message: String) -> Unit
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