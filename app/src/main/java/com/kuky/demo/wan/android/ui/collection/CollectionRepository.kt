package com.kuky.demo.wan.android.ui.collection

import android.content.Context
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * @author: kuky
 * @description
 */
class CollectionRepository(private val context: Context, private val api: ApiService) {
    suspend fun collectArticle(id: Int) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(context)
            api.collectArticleOrProject(id, cookie)
        }
}
