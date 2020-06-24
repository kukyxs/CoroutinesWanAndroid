package com.kuky.demo.wan.android.ui.collection

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext


/**
 * @author: kuky
 * @description
 */
class CollectionRepository {
    private val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun collectArticle(id: Int) = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.collectArticleOrProject(id, cookie)
    }

    fun getCollectArticleResultStream(id: Int) = flow {
        emit(collectArticle(id))
    }
}
