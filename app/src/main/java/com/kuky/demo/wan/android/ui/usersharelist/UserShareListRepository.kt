package com.kuky.demo.wan.android.ui.usersharelist

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class UserShareListRepository {

    suspend fun fetchUserShareList(page: Int): MutableList<UserArticleDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.userShareList(
                page, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data.shareArticles.datas
        }

    suspend fun deleteShare(id: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.deleteAShare(
                id, PreferencesHelper.fetchCookie(WanApplication.instance)
            )
        }

    suspend fun shareArticle(title: String, link: String) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.putAShare(
                title, link, PreferencesHelper.fetchCookie(WanApplication.instance)
            )
        }
}