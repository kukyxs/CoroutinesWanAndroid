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
    var articles: LiveData<PagedList<ArticleDetail>>? = null

    fun fetchHomeArticles() {
        articles = LivePagedListBuilder(
            HomeArticleDataSourceFactory(HomeArticleRepository()),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}