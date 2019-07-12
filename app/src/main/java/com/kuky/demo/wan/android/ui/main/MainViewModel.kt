package com.kuky.demo.wan.android.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.data.MainRepository
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

    fun login(username: String, password: String) {
        viewModelScope.safeLaunch {
            hasLogin.value = repository.login(username, password)
        }
    }

    fun register(username: String, password: String, repass: String) {
        viewModelScope.safeLaunch {
            hasLogin.value = repository.register(username, password, repass)
        }
    }

    fun loginout() {
        viewModelScope.safeLaunch {
            hasLogin.value = !repository.loginout()
        }
    }
}