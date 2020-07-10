package com.kuky.demo.wan.android.ui.usersharelist

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class UserShareListRepository(private val api: ApiService) {

    suspend fun fetchUserShareList(page: Int): MutableList<UserArticleDetail>? =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.userShareList(page, cookie).data.shareArticles.datas
        }

    suspend fun deleteShare(id: Int) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.deleteAShare(id, cookie)
        }

    suspend fun shareArticle(title: String, link: String) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.putAShare(title, link, cookie)
        }
}