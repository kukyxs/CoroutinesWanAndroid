package com.kuky.demo.wan.android.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.safeLaunch


/**
 * Author: Taonce
 * Date: 2019/8/1
 * Desc: 收藏文章VM
 */
class CollectionViewModel(private val repo: CollectionRepository) : ViewModel() {
    fun collectArticle(id: Int, success: () -> Unit, fail: (String) -> Unit) {
        viewModelScope.safeLaunch({
            repo.collectArticle(id).let {
                if (it.errorCode == 0) success()
                else fail(it.errorMsg)
            }
        }, { fail("网络出错啦~请检查您的网络") })
    }
}
