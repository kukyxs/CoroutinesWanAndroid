package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.entity.WxChapterListDatas


/**
 * @author Taonce.
 * @description
 */
class WxChapterListViewModel(private val repository: WxChapterListRepository) : ViewModel() {

    var chapters: LiveData<PagedList<WxChapterListDatas>>? = null

    fun fetchResult(wxId: Int) {
        chapters = LivePagedListBuilder(
            WxChapterListDataSourceFactory(repository, wxId),
            PagedList.Config.Builder().setPageSize(20)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}

