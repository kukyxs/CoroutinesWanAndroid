package com.kuky.demo.wan.android.ui.shareduser

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.PagingThrowableHandler
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.SharedData
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */
class SharedUserViewModel(private val repository: UserSharedRepository) : ViewModel() {

    var articles: LiveData<PagedList<UserArticleDetail>>? = null
    var userCoin = MutableLiveData<SharedData?>()

    fun fetchSharedArticles(handler: PagingThrowableHandler, userId: Int) {
        articles = LivePagedListBuilder(
            UserSharedDataSourceFactory(repository, userId, handler),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun fetchUserInfo(userId: Int) {
        viewModelScope.safeLaunch(null, {
            userCoin.value = repository.fetchUserCoinInfo(userId)
        })
    }
}