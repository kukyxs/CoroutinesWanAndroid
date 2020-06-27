package com.kuky.demo.wan.android.ui.shareduser

import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class UserSharedRepository {
    suspend fun fetchUserSharedArticles(userId: Int, page: Int):
            MutableList<UserArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.sharedUserInfo(userId, page, cookie).data.shareArticles.datas
    }

    suspend fun fetchUserCoinInfo(userId: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.sharedUserInfo(userId, 1, cookie).data
        }
}