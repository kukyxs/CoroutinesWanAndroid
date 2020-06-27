package com.kuky.demo.wan.android.ui.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.TodoInfo
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * @author kuky.
 * @description
 */
class TodoListViewModel(private val repository: TodoRepository) : ViewModel() {

    private var mCurrentParam: HashMap<String, Int>? = null
    private var mCurrentTodoResult: Flow<PagingData<TodoInfo>>? = null

    fun getTodoList(param: HashMap<String, Int>): Flow<PagingData<TodoInfo>> {
        val lastResult = mCurrentTodoResult
        if (param == mCurrentParam && lastResult != null) return lastResult

        mCurrentParam = param
        return Pager(constPagerConfig) {
            TodoPagingSource(repository, param)
        }.flow.apply {
            mCurrentTodoResult = this
        }.cachedIn(viewModelScope)
    }

    fun updateTodoState(id: Int, state: Int) = flow {
        emit(repository.updateTodoState(id, state))
    }

    fun updateTodoState(id: Int, state: Int, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            block = {
                repository.updateTodoState(id, state).let {
                    if (it.errorCode == 0) success()
                    else fail(it.errorMsg)
                }
            }
        }
    }
}