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
    suspend fun getCollectedArticleList(page: Int): MutableList<UserCollectDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.userCollectedArticles(
                page, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data.datas
        }

    suspend fun removeCollectedArticle(articleId: Int, originId: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.unCollectCollection(
                articleId, originId, PreferencesHelper.fetchCookie(WanApplication.instance)
            )
        }
}