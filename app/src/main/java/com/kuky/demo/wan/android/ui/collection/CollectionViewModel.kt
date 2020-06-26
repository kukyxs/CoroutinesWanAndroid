package com.kuky.demo.wan.android.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.safeLaunch
import kotlinx.coroutines.flow.flow


/**
 * @author kuky.
 * @description
 */
class CollectionViewModel(private val repository: CollectionRepository) : ViewModel() {

    fun collectArticle(id: Int) = flow {
        emit(repository.collectArticle(id))
    }

    fun collectArticle(id: Int, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch {
            block = {
                repository.collectArticle(id).let {
                    if (it.errorCode == 0) success()
                    else fail(it.errorMsg)
                }
            }
            onError = {
                fail("网络出错啦~请检查您的网络")
            }
        }
    }
}
