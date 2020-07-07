package com.kuky.demo.wan.android.ui.collection

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * @author: kuky
 * @description
 */
class CollectionRepository(private val api: ApiService) {
    suspend fun collectArticle(id: Int) =
        withContext(Dispatchers.IO) {
            api.collectArticleOrProject(
                id, PreferencesHelper.fetchCookie(WanApplication.instance)
            )
        }
}
