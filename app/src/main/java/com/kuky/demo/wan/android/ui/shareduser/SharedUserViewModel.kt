package com.kuky.demo.wan.android.ui.shareduser

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.SharedData
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */
class SharedUserViewModel(private val repository: UserSharedRepository) : ViewModel() {

    var netState: LiveData<NetworkState>? = null
    var articles: LiveData<PagedList<UserArticleDetail>>? = null
    var userCoin = MutableLiveData<SharedData?>()

    fun fetchSharedArticles(userId: Int, empty: () -> Unit) {
        articles = LivePagedListBuilder(
            UserSharedDataSourceFactory(repository, userId).apply {
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

    fun fetchUserInfo(userId: Int) {
        viewModelScope.safeLaunch {
            block = {
                userCoin.value = repository.fetchUserCoinInfo(userId)
            }
        }
    }
}