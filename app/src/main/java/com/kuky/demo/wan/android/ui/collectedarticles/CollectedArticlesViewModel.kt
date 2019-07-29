package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.UserCollectDetail


/**
 * Author: Taonce
 * Date: 2019/7/19
 * Project: CoroutinesWanAndroid
 * Desc:
 */
class CollectedArticlesViewModel(private val repo: CollectedArticlesRepository) : ViewModel() {
    var mArticles: LiveData<PagedList<UserCollectDetail>>? = null

    fun fetchCollectedArticleDatas() {
        mArticles = LivePagedListBuilder(
            CollectedArticlesDataSourceFactory(repo),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun deleteCollectedArticle(
        articleId: Int,
        originId: Int,
        onSuccess: () -> Unit,
        onFailed: (errorMsg: String) -> Unit
    ) {
        viewModelScope.safeLaunch {
            val result = repo.deleteCollectedArticle(articleId, originId)
            if (result.errorCode == 0) {
                // TODO("目前根据官方文档，通过 dataSource.invalidate 刷新 Paging 数据")
                mArticles?.value?.dataSource?.invalidate()
                onSuccess()
            } else {
                onFailed(result.errorMsg)
            }
        }
    }
}

