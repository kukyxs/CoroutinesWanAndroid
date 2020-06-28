package com.kuky.demo.wan.android.ui.search

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class SearchRepository {

    // 搜索热词
    suspend fun hotKeys() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.hotKeys().data
        }

    // 搜索结果
    suspend fun loadSearchResult(page: Int, key: String): MutableList<ArticleDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.searchArticle(
                page, key, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data.datas
        }
}