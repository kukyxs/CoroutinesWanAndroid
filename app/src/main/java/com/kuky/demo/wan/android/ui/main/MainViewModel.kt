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
@Suppress("MemberVisibilityCanBePrivate")
class MainViewModel(private val repository: MainRepository) : ViewModel() {
    val hasLogin = MutableLiveData<Boolean>()
    val banners = MutableLiveData<List<BannerData>>()

    fun getBanners() {
        viewModelScope.safeLaunch {
            banners.value = repository.getHomeBanners()
        }
    }
}