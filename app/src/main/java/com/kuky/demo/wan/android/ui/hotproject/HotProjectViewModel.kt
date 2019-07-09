package com.kuky.demo.wan.android.ui.hotproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.entity.ProjectDetailData

/**
 * @author kuky.
 * @description
 */
class HotProjectViewModel : ViewModel() {

    val projects: LiveData<PagedList<ProjectDetailData>> by lazy {
        LivePagedListBuilder(
            HotProjectDataSourceFactory(HotProjectRepository()),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}