package com.kuky.demo.wan.android.ui.collection

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.CODE_FAILED
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.ResultBack
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Author: Taonce
 * Date: 2019/8/1
 * Desc: 收藏文章Repo
 */
class CollectionRepository {
    suspend fun collectArticle(id: Int): ResultBack = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService
            .collectArticleOrProject(id, PreferencesHelper.fetchCookie(WanApplication.instance))

        suspendCoroutine<ResultBack> { continuation ->
            if (result.errorCode == 0) continuation.resume(ResultBack(CODE_SUCCEED, ""))
            else continuation.resume(ResultBack(CODE_FAILED, result.errorMsg))
        }
    }
}
