package com.kuky.demo.wan.android.ui.userarticles

import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class UserArticleRepository {
    suspend fun fetchUserArticles(page: Int): MutableList<UserArticleDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.shareArticles(page, cookie).data.datas
        }
}