package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.kuky.demo.wan.android.base.safeLaunch


/**
 * Author: Taonce
 * Date: 2019/7/19
 * Project: CoroutinesWanAndroid
 * Desc:
 */
class CollectedArticlesViewModel(private val repo: CollectedArticlesRepository) : ViewModel() {

    val articleList = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = true, prefetchDistance = 5)
    ) { CollectedArticlesPagingSource(repo) }.flow

    fun removeCollectedArticle(
        articleId: Int, originId: Int,
        onSuccess: suspend () -> Unit, onFailed: (errorMsg: String) -> Unit
    ) {
        viewModelScope.safeLaunch {
            block = {
                val result = repo.removeCollectedArticle(articleId, originId)

                if (result.errorCode == 0) {
                    onSuccess()
                } else {
                    onFailed(result.errorMsg)
                }
            }

            onError = { onFailed("网络出错啦~请检查网络") }
        }
    }
}

