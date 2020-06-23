package com.kuky.demo.wan.android.ui.home

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class HomeArticleRepository {

    suspend fun loadPageData(page: Int): List<HomeArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.homeArticles(page).data.datas
    }

    // 加载首页置顶文章
    suspend fun loadTops(): List<HomeArticleDetail>? = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.topArticle(PreferencesHelper.fetchCookie(WanApplication.instance)).data
    }
}