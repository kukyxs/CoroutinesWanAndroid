package com.kuky.demo.wan.android.ui.collection

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Author: Taonce
 * Date: 2019/8/1
 * Desc: 收藏文章Repo
 */
class CollectionRepository {
    suspend fun collectArticle(id: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService
            .collectArticleOrProject(id, PreferencesHelper.fetchCookie(WanApplication.instance))
    }
}
