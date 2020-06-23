package com.kuky.demo.wan.android.ui.collectedarticles

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.UserCollectDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesRepository {
    private fun getCookie() = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun getCollectedArticleList(page: Int): List<UserCollectDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.userCollectedArticles(page, getCookie()).data.datas
    }

    suspend fun removeCollectedArticle(articleId: Int, originId: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.unCollectCollection(articleId, originId, getCookie())
    }
}