package com.kuky.demo.wan.android.ui.hotproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData

/**
 * @author kuky.
 * @description
 */
class HotProjectViewModel(private val repository: HotProjectRepository) : ViewModel() {

    val categories: MutableLiveData<List<ProjectCategoryData>> = MutableLiveData()
    var projects: LiveData<PagedList<ProjectDetailData>>? = null

    fun fetchCategories() {
        viewModelScope.safeLaunch {
            categories.value = repository.loadProjectCategories()
        }
    }

    fun fetchDiffCategoryProjects(pid: Int) {
        projects = LivePagedListBuilder(
            HotProjectDataSourceFactory(HotProjectRepository(), pid),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun collectProject(id: Int, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            val result = repository.collectProject(id)
            if (result.code == CODE_SUCCEED)
                success()
            else
                fail(result.message)
        }
    }
}