package com.kuky.demo.wan.android.ui.usersharelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class UserShareListViewModel(
    private val repository: UserShareListRepository
) : ViewModel() {

    fun getSharedArticles() = Pager(constPagerConfig) {
        UserSharePagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun deleteAShare(id: Int) = flow {
        emit(repository.deleteShare(id))
    }

    fun putAShare(title: String, link: String) = flow {
        emit(repository.shareArticle(title, link))
    }
}