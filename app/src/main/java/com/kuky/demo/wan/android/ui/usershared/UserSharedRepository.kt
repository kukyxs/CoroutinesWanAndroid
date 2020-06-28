package com.kuky.demo.wan.android.ui.usershared

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
class UserSharedRepository {
    suspend fun fetchUserSharedArticles(userId: Int, page: Int): MutableList<UserArticleDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.sharedUserInfo(
                userId, page, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data.shareArticles.datas
        }

    suspend fun fetchUserCoinInfo(userId: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.sharedUserInfo(
                userId, 1, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data
        }
}