package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.entity.ArticleDetail

/**
 * @author kuky.
 * @description
 */
class HomeViewModel : ViewModel() {
    val articles: LiveData<PagedList<ArticleDetail>> by lazy {
        LivePagedListBuilder(
            HomeArticleDataSourceFactory(HomeArticleRepository()),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setPrefetchDistance(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}