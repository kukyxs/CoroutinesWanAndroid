package com.kuky.demo.wan.android.ui.collectedarticles

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.entity.UserCollectDetail


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
}

