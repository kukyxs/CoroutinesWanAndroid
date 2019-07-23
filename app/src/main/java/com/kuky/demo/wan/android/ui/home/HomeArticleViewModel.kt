package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.ArticleDetail

/**
 * @author kuky.
 * @description
 */
class HomeArticleViewModel(private val repository: HomeArticleRepository) : ViewModel() {
    var articles: LiveData<PagedList<ArticleDetail>>? = null

    fun fetchHomeArticle() {
        articles = LivePagedListBuilder(
            HomeArticleDataSourceFactory(HomeArticleRepository()),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun collectArticle(id: Int, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            val result = repository.collectArticle(id)

            if (result.code == CODE_SUCCEED)
                success()
            else
                fail(result.message)
        }
    }
}