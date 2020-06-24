package com.kuky.demo.wan.android.ui.hotproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.ProjectCategoryData
import com.kuky.demo.wan.android.entity.ProjectDetailData
import kotlinx.coroutines.flow.Flow

/**
 * @author kuky.
 * @description
 */
class HotProjectViewModel(private val repository: HotProjectRepository) : ViewModel() {

    val typeNetState = MutableLiveData<NetworkState>()
    val categories = MutableLiveData<List<ProjectCategoryData>>()
    val selectedCategoryPosition = MutableLiveData<Int>()

    private var currentPid: Int? = null
    var currentProResult: Flow<PagingData<ProjectDetailData>>? = null

    fun getDiffCategoryProjects(pid: Int): Flow<PagingData<ProjectDetailData>> {
        val lastResult = currentProResult
        if (currentPid == pid && lastResult != null) return lastResult
        currentPid = pid
        return repository.getProjectsStream(pid).apply { currentProResult = this }.cachedIn(viewModelScope)
    }

    init {
        selectedCategoryPosition.value = 0
    }

    fun fetchCategories() {
        viewModelScope.safeLaunch {
            block = {
                typeNetState.postValue(NetworkState.LOADING)
                categories.value = repository.loadProjectCategories()
                typeNetState.postValue(NetworkState.LOADED)
            }

            onError = {
                typeNetState.postValue(NetworkState.error(it.message))
            }
        }
    }
}