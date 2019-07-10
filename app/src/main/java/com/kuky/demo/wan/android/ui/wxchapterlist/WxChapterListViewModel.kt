package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList


/**
 * @author Taonce.
 * @description
 */
class WxChapterListViewModel : ViewModel() {

    fun fetchResult(wxId: Int) =
        LivePagedListBuilder(
            WxChapterListDataSourceFactory(WxChapterListRepository(), wxId),
            PagedList.Config.Builder().setPageSize(20)
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
}

