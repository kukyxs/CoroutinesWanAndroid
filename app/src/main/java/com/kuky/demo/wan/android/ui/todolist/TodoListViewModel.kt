package com.kuky.demo.wan.android.ui.todolist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.TodoInfo

/**
 * @author kuky.
 * @description
 */
class TodoListViewModel(private val repository: TodoRepository) : ViewModel() {

    var todoList: LiveData<PagedList<TodoInfo>>? = null

    fun fetchTodoList(param: HashMap<String, Int>) {
        todoList = LivePagedListBuilder(
            TodoDataSourceFactory(repository, param),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun updateTodoState(id: Int, state: Int, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            repository.updateTodoState(id, state).let {
                if (it.code == CODE_SUCCEED) success()
                else fail(it.message)
            }
        }
    }
}