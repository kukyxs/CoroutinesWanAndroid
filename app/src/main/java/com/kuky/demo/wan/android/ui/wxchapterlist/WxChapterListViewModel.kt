package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.entity.WxChapterListDatas


/**
 * @author Taonce.
 * @description
 */
class WxChapterListViewModel(private val repository: WxChapterListRepository) : ViewModel() {

    var netState: LiveData<NetworkState>? = null
    var chapters: LiveData<PagedList<WxChapterListDatas>>? = null

    fun fetchWxArticles(wxId: Int, keyword: String, empty: () -> Unit) {
        chapters = LivePagedListBuilder(
            WxChapterListDataSourceFactory(repository, wxId, keyword).apply {
                netState = Transformations.switchMap(sourceLiveData) { it.initState }
            }, PagedList.Config.Builder().setPageSize(20)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .build()
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<WxChapterListDatas>() {
            override fun onZeroItemsLoaded() = empty()
        }).build()
    }
}

