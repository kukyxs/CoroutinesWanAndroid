package com.kuky.demo.wan.android.ui.collectedwebsites

import android.content.Context
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesRepository(private val context: Context, private val api: ApiService) {
    suspend fun getCollectedWebsites(): MutableList<WebsiteData> =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(context)
            api.collectWebsiteList(cookie).data ?: mutableListOf()
        }

    suspend fun addWebsite(name: String, link: String) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(context)
            api.addWebsite(name, link, cookie)
        }

    suspend fun editWebsite(id: Int, name: String, link: String) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(context)
            api.editWebsite(id, name, link, cookie)
        }

    suspend fun deleteWebsite(id: Int) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(context)
            api.deleteWebsite(id, cookie)
        }
}