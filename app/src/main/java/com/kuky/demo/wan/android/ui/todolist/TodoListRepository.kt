package com.kuky.demo.wan.android.ui.todolist

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class TodoListRepository(private val api: ApiService) {

    suspend fun fetchTodoList(page: Int, param: HashMap<String, Int>): MutableList<TodoInfo>? =
        withContext(Dispatchers.IO) {
            api.fetchTodoList(
                page, PreferencesHelper.fetchCookie(WanApplication.instance), param
            ).data.datas
        }

    suspend fun updateTodoState(id: Int, state: Int) =
        withContext(Dispatchers.IO) {
            api.updateTodoState(
                id, state, PreferencesHelper.fetchCookie(WanApplication.instance)
            )
        }
}