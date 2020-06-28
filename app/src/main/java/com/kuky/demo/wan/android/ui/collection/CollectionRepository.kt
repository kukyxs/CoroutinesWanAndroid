package com.kuky.demo.wan.android.ui.collection

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * @author: kuky
 * @description
 */
class CollectionRepository {
    suspend fun collectArticle(id: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.collectArticleOrProject(
                id, PreferencesHelper.fetchCookie(WanApplication.instance)
            )
        }
}
