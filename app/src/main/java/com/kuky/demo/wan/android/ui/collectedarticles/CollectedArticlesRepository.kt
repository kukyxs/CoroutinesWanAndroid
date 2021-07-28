package com.kuky.demo.wan.android.ui.collectedarticles

import android.content.Context
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.UserCollectDetail
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesRepository(private val context: Context, private val api: ApiService) {
    suspend fun getCollectedArticleList(page: Int): MutableList<UserCollectDetail> =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(context)
            api.userCollectedArticles(page, cookie).data?.datas ?: mutableListOf()
        }

    suspend fun removeCollectedArticle(articleId: Int, originId: Int) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(context)
            api.unCollectCollection(articleId, originId, cookie)
        }
}