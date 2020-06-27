package com.kuky.demo.wan.android.ui.todoedit

import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class TodoEditRepository {

    suspend fun addTodo(param: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.addTodo(param, cookie)
        }

    suspend fun updateTodo(id: Int, param: HashMap<String, Any>) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.updateTodo(id, cookie, param)
        }

    suspend fun deleteTodo(id: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.deleteTodo(id, cookie)
        }
}