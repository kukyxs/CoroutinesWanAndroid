package com.kuky.demo.wan.android.ui.userarticles

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */
class UserArticleViewModel(private val repository: UserArticleRepository) : ViewModel() {

    var netState: LiveData<NetworkState>? = null
    var userArticles: LiveData<PagedList<UserArticleDetail>>? = null

    fun fetchSharedArticles(empty: () -> Unit) {
        userArticles = LivePagedListBuilder(
            UserArticleDataSourceFactory(repository).apply {
                netState = Transformations.switchMap(sourceLiveData) { it.initState }
            }, PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<UserArticleDetail>() {
            override fun onZeroItemsLoaded() = empty()
        }).build()
    }
}