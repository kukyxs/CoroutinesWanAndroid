package com.kuky.demo.wan.android.ui.collectedwebsites

import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesRepository {
    suspend fun getCollectedWebsites(): MutableList<WebsiteData>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.collectWebsiteList(cookie).data
        }

    suspend fun addWebsite(name: String, link: String) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.addWebsite(name, link, cookie)
        }

    suspend fun editWebsite(id: Int, name: String, link: String) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.editWebsite(id, name, link, cookie)
        }

    suspend fun deleteWebsite(id: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.deleteWebsite(id, cookie)
        }
}