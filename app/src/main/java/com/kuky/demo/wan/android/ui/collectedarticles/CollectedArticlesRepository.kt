package com.kuky.demo.wan.android.ui.collectedarticles

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.UserCollectDetail
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesRepository(private val api: ApiService) {
    suspend fun getCollectedArticleList(page: Int): MutableList<UserCollectDetail>? =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.userCollectedArticles(page, cookie).data.datas
        }

    suspend fun removeCollectedArticle(articleId: Int, originId: Int) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.unCollectCollection(articleId, originId, cookie)
        }
}