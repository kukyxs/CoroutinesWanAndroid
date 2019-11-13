package com.kuky.demo.wan.android.ui.usersharelist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.PagingThrowableHandler
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.UserArticleDetail

/**
 * @author kuky.
 * @description
 */
class UserShareListViewModel(private val repository: UserShareListRepository) : ViewModel() {

    var articles: LiveData<PagedList<UserArticleDetail>>? = null

    fun fetchSharedArticles(handler: PagingThrowableHandler) {
        articles = LivePagedListBuilder(
            UserShareDataSourceFactory(repository, handler),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun deleteAShare(id: Int, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch({
            repository.deleteShare(id).let {
                if (it.errorCode == 0) {
                    articles?.value?.dataSource?.invalidate()
                    success()
                } else fail(it.errorMsg)
            }
        }, { fail("删除出错啦~请检查网络") })
    }

    fun putAShare(title: String, link: String, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch({
            if (title.isBlank() || link.isBlank())
                fail("请填写必要信息")
            else repository.shareArticle(title, link).let {
                if (it.errorCode == 0) {
                    articles?.value?.dataSource?.invalidate()
                    success()
                } else fail(it.errorMsg)
            }
        }, { fail("分享出错啦~请检查网络") })
    }
}