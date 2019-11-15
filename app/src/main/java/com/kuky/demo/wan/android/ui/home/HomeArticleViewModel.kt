package com.kuky.demo.wan.android.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.data.db.WanDatabaseUtils

/**
 * @author kuky.
 * @description
 */
class HomeArticleViewModel(private val repository: HomeArticleRepository) : ViewModel() {
    var netState: LiveData<NetworkState>? = null
    var cache: LiveData<List<HomeArticleDetail>>? = null
    var articles: LiveData<PagedList<HomeArticleDetail>>? = null

    fun fetchHomeArticle(empty: () -> Unit) {
        articles = LivePagedListBuilder(
            HomeArticleDataSourceFactory(repository).apply {
                netState = Transformations.switchMap(this.sourceLiveData) { it.initState }
            }, PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .build()
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<HomeArticleDetail>() {
            override fun onZeroItemsLoaded() = empty()
        }).build()
    }

    fun refreshArticle() = articles?.value?.dataSource?.invalidate()

    fun fetchCache() {
        cache = WanDatabaseUtils.homeArticleCacheDao.fetchAllCache()
    }
}