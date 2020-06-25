package com.kuky.demo.wan.android.ui.collectedwebsites

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.WebsiteData
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedWebsitesRepository {
    private val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun getCollectedWebsites(): MutableList<WebsiteData>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.collectWebsiteList(cookie).data
    }

    private suspend fun addWebsite(name: String, link: String) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.addWebsite(name, link, cookie)
    }

    private suspend fun editWebsite(id: Int, name: String, link: String) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.editWebsite(id, name, link, cookie)
    }

    private suspend fun deleteWebsite(id: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.deleteWebsite(id, cookie)
    }

    fun getCollectedWebsitesStream() = flow {
        emit(getCollectedWebsites())
    }

    fun getAddWebsiteResultStream(name: String, link: String) = flow {
        emit(addWebsite(name, link))
    }

    fun getEditWebsiteResultStream(id: Int, name: String, link: String) = flow {
        emit(editWebsite(id, name, link))
    }

    fun getDeleteWebsiteResultStream(id: Int) = flow {
        emit(deleteWebsite(id))
    }
}