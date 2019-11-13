package com.kuky.demo.wan.android.ui.userarticles

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.PagingThrowableHandler
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */
class UserArticleViewModel(private val repository: UserArticleRepository) : ViewModel() {

    var userArticles: LiveData<PagedList<UserArticleDetail>>? = null

    fun fetchSharedArticles(handler: PagingThrowableHandler) {
        userArticles = LivePagedListBuilder(
            UserArticleDataSourceFactory(repository, handler),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}