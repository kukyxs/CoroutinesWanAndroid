package com.kuky.demo.wan.android.ui.todoedit

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class TodoEditRepository(private val api: ApiService) {

    suspend fun addTodo(param: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.addTodo(param, cookie)
        }

    suspend fun updateTodo(id: Int, param: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.updateTodo(id, cookie, param)
        }

    suspend fun deleteTodo(id: Int) =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.deleteTodo(id, cookie)
        }
}