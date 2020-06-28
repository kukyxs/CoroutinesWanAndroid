package com.kuky.demo.wan.android.ui.todolist

import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class TodoListRepository {
    suspend fun fetchTodoList(page: Int, param: HashMap<String, Int>): MutableList<TodoInfo>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.fetchTodoList(page, cookie, param).data.datas
        }

    suspend fun updateTodoState(id: Int, state: Int) =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.updateTodoState(id, state, cookie)
        }
}