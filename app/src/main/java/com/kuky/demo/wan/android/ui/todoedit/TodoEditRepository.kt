package com.kuky.demo.wan.android.ui.todoedit

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.CODE_FAILED
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.ResultBack
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.BasicResultData
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author kuky.
 * @description
 */
class TodoEditRepository {

    suspend fun addTodo(param: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        val result = RetrofitManager
            .apiService.addTodo(param, PreferencesHelper.fetchCookie(WanApplication.instance))

        handleResult(result)
    }

    suspend fun updateTodo(id: Int, param: HashMap<String, Any>) = withContext(Dispatchers.IO) {
        val result = RetrofitManager
            .apiService.updateTodo(id, PreferencesHelper.fetchCookie(WanApplication.instance), param)

        handleResult(result)
    }

    suspend fun deleteTodo(id: Int) = withContext(Dispatchers.IO) {
        val result = RetrofitManager
            .apiService.deleteTodo(id, PreferencesHelper.fetchCookie(WanApplication.instance))

        handleResult(result)
    }

    private suspend fun handleResult(result: BasicResultData): ResultBack {
        return suspendCoroutine<ResultBack> { continuation ->
            if (result.errorCode == 0) continuation.resume(ResultBack(CODE_SUCCEED, ""))
            else continuation.resume(ResultBack(CODE_FAILED, result.errorMsg))
        }
    }
}