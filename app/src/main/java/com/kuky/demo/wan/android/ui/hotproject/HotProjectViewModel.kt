package com.kuky.demo.wan.android.ui.hotproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class HotProjectViewModel(private val repository: HotProjectRepository) : ViewModel() {

    val selectedCategoryPosition = MutableLiveData<Int>()

    private var currentPid: Int? = null
    private var currentProResult: Flow<PagingData<ProjectDetailData>>? = null
    private var currentCategoriesResult: Flow<MutableList<ProjectCategoryData>>? = null

    init {
        selectedCategoryPosition.value = 0
    }

    fun getCategories(): Flow<MutableList<ProjectCategoryData>> {
        val lastResult = currentCategoriesResult
        if (lastResult != null) return lastResult

        return flow {
            emit(repository.loadProjectCategories())
        }.apply { currentCategoriesResult = this }
    }

    fun getDiffCategoryProjects(pid: Int): Flow<PagingData<ProjectDetailData>> {
        val lastResult = currentProResult
        if (currentPid == pid && lastResult != null) return lastResult
        currentPid = pid

        return Pager(constPagerConfig) {
            HotProjectPagingSource(repository, pid)
        }.flow.apply {
            currentProResult = this
        }.cachedIn(viewModelScope)
    }
}