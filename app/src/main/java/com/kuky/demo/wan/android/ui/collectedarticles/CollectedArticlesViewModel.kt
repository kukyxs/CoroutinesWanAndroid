package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.UserCollectDetail
import org.jetbrains.anko.toast


/**
 * Author: Taonce
 * Date: 2019/7/19
 * Project: CoroutinesWanAndroid
 * Desc:
 */
class CollectedArticlesViewModel(private val repo: CollectedArticlesRepository) : ViewModel() {
    var articles: LiveData<PagedList<UserCollectDetail>>? = null

    fun fetchCollectedArticleDatas() {
        articles = LivePagedListBuilder(
            CollectedArticlesDataSourceFactory(CollectedArticlesRepository()),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun deleteCollectedArticle(articleId: Int, originId: Int, onSuccess: () -> Unit) {
        viewModelScope.safeLaunch {
            val result = repo.deleteCollectedArticle(articleId, originId)
            if (result.errorCode == 0) {
                WanApplication.instance.toast("取消成功")
                onSuccess()
            } else {
                WanApplication.instance.toast(result.errorMsg)
            }
        }
    }
}

