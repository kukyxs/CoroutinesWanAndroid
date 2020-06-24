package com.kuky.demo.wan.android.ui.hotproject

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class HotProjectRepository {
    private val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)

    // 加载分类
    suspend fun loadProjectCategories() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.projectCategory().data
        }

    // 加载分类下的项目列表
    suspend fun loadProjects(page: Int, pid: Int): List<ProjectDetailData>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.projectList(page, pid, cookie).data.datas
        }

    fun getProjectsStream(pid: Int) = Pager(
        config = PagingConfig(pageSize = 20)
    ) { HotProjectPagingSource(this, pid) }.flow
}