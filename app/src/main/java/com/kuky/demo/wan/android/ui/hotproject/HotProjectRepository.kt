package com.kuky.demo.wan.android.ui.hotproject

import com.kuky.demo.wan.android.entity.ProjectDetailData
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class HotProjectRepository {
    // 加载分类
    suspend fun loadProjectCategories() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.projectCategory().data
        }

    // 加载分类下的项目列表
    suspend fun loadProjects(page: Int, pid: Int): MutableList<ProjectDetailData>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.projectList(page, pid, cookie).data.datas
        }
}