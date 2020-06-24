package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.UserCollectDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CollectedArticlesRepository {
    private val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun getCollectedArticleList(page: Int): List<UserCollectDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.userCollectedArticles(page, cookie).data.datas
        }

    private suspend fun removeCollectedArticle(articleId: Int, originId: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.unCollectCollection(articleId, originId, cookie)
        }

    fun getCollectedArticlesStream() = Pager(
        config = PagingConfig(pageSize = 20)
    ) { CollectedArticlesPagingSource(this) }.flow

    fun getRemoveResultStream(articleId: Int, originId: Int) = flow {
        emit(removeCollectedArticle(articleId, originId))
    }
}