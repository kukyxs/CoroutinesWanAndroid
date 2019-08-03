package com.kuky.demo.wan.android.ui.todoedit

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.utils.LogUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class TodoEditRepository {

    suspend fun addTodo(param: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.addTodo(param, PreferencesHelper.fetchCookie(WanApplication.instance))
        LogUtils.error(result.string())
    }

    suspend fun updateTodo(id: Int, param: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        val result = RetrofitManager
            .apiService.updateTodo(id, PreferencesHelper.fetchCookie(WanApplication.instance), param)

        LogUtils.error(result.string())
    }

    suspend fun deleteTodo(id: Int) = withContext(Dispatchers.IO) {
        val result = RetrofitManager.apiService.deleteTodo(id, PreferencesHelper.fetchCookie(WanApplication.instance))

        LogUtils.error(result.string())
    }
}