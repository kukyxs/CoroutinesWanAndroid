package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.CoroutineThrowableHandler
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.WebsiteData

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesViewModel(private val repo: CollectedWebsitesRepository) : ViewModel() {
    val mWebsitesData = MutableLiveData<List<WebsiteData>?>()

    fun fetchWebSitesData(handler: CoroutineThrowableHandler? = null) {
        viewModelScope.safeLaunch({
            mWebsitesData.value = repo.getCollectedWebsites()
        }, { handler?.invoke(it) })
    }

    fun addWebsites(
        name: String?, link: String?,
        success: () -> Unit, fail: (msg: String, isDismiss: Boolean) -> Unit
    ) {
        if (name.isNullOrBlank() || link.isNullOrBlank()) {
            fail("输入不可为空!", false)
        } else {
            viewModelScope.safeLaunch({
                repo.addWebsite(name, link).let {
                    if (it.errorCode == 0) success()
                    else fail(it.errorMsg, true)
                }
            }, { fail("网络出错啦~请检查网络", false) })
        }
    }

    fun editWebsite(
        id: Int, name: String, link: String,
        success: () -> Unit, fail: (msg: String, isDismiss: Boolean) -> Unit
    ) {
        if (name.isBlank() || link.isBlank()) {
            fail("输入不可为空", false)
        } else {
            viewModelScope.safeLaunch({
                repo.editWebsite(id, name, link).let {
                    if (it.errorCode == 0) success()
                    else fail(it.errorMsg, true)
                }
            }, { fail("网络出错啦~请检查网络", false) })
        }
    }

    fun deleteWebsite(id: Int, onSuccess: () -> Unit, onFailed: (errorMsg: String) -> Unit) {
        viewModelScope.safeLaunch({
            val result = repo.deleteWebsite(id)
            if (result.errorCode == 0) {
                onSuccess()
            } else onFailed(result.errorMsg)
        }, { onFailed("网络出错啦~请检查网络") })
    }
}
