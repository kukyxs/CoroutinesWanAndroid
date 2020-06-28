package com.kuky.demo.wan.android.ui.hotproject

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
    // 加载分类
    suspend fun loadProjectCategories() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.projectCategory().data
        }

    // 加载分类下的项目列表
    suspend fun loadProjects(page: Int, pid: Int): MutableList<ProjectDetailData>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.projectList(
                page, pid, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data.datas
        }
}