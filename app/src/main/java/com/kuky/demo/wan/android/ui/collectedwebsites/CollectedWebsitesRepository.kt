package com.kuky.demo.wan.android.ui.collectedwebsites

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesRepository {
    private fun getCookie() = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun getCollectedWebsites(): List<WebsiteData>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.collectWebsiteList(getCookie()).data
    }

    suspend fun addWebsite(name: String, link: String) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.addWebsite(name, link, getCookie())
    }

    suspend fun editWebsite(id: Int, name: String, link: String) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.editWebsite(id, name, link, PreferencesHelper.fetchCookie(WanApplication.instance))
    }

    suspend fun deleteWebsite(id: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.deleteWebsite(id, getCookie())
    }
}