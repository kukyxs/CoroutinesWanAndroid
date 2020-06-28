package com.kuky.demo.wan.android.ui.usershared

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.entity.UserArticleDetail
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class UserSharedViewModel(
    private val repository: UserSharedRepository
) : ViewModel() {

    private var mCurrentUserId: Int? = null
    private var mCurrentArticleResult: Flow<PagingData<UserArticleDetail>>? = null

    fun getSharedArticles(userId: Int): Flow<PagingData<UserArticleDetail>> {
        val lastResult = mCurrentArticleResult
        if (mCurrentUserId == userId && lastResult != null) return lastResult

        mCurrentUserId = userId
        return Pager(constPagerConfig) {
            UserSharedPagingSource(repository, userId)
        }.flow.apply {
            mCurrentArticleResult = this
        }.cachedIn(viewModelScope)
    }

    fun getUserCoinInfo(userId: Int) = flow {
        emit(repository.fetchUserCoinInfo(userId))
    }
}