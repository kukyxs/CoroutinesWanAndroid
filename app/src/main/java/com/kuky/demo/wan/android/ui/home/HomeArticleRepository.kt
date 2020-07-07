package com.kuky.demo.wan.android.ui.home

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.data.WanDatabase
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class HomeArticleRepository(private val api: ApiService, val db: WanDatabase) {

    val pagingSourceFactory = { db.homeArticleCacheDao().fetchAllHomeArticleCache() }

    suspend fun loadPageData(page: Int): MutableList<HomeArticleDetail>? =
        withContext(Dispatchers.IO) {
            api.homeArticles(page).data.datas
        }

    // 加载首页置顶文章
    suspend fun loadTops(): MutableList<HomeArticleDetail>? =
        withContext(Dispatchers.IO) {
            api.topArticle(PreferencesHelper.fetchCookie(WanApplication.instance)).data
        }
}