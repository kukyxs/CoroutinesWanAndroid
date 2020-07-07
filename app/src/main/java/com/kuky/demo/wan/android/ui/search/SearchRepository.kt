package com.kuky.demo.wan.android.ui.search

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.ArticleDetail
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class SearchRepository(private val api: ApiService) {

    // 搜索热词
    suspend fun hotKeys() =
        withContext(Dispatchers.IO) {
            api.hotKeys().data
        }

    // 搜索结果
    suspend fun loadSearchResult(page: Int, key: String): MutableList<ArticleDetail>? =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.searchArticle(page, key, cookie).data.datas
        }
}