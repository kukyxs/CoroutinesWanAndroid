package com.kuky.demo.wan.android.ui.collectedwebsites

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.CODE_SUCCEED
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
            handler?.invoke(it)
        }, {
            mWebsitesData.value = repo.getCollectedWebsites()
        })
    }

    fun addWebsites(
        name: String?, link: String?,
        success: () -> Unit, failed: (msg: String, isDismiss: Boolean) -> Unit
    ) {
        if (name.isNullOrBlank() || link.isNullOrBlank()) {
            failed("输入不可为空!", false)
        } else {
            viewModelScope.safeLaunch({
                failed("网络出错啦~请检查网络", false)
            }, {
                val result = repo.addWebsite(name, link)

                if (result.code == CODE_SUCCEED) success()
                else failed(result.message, true)
            })
        }
    }

    fun deleteWebsite(id: Int, onSuccess: () -> Unit, onFailed: (errorMsg: String) -> Unit) {
        viewModelScope.safeLaunch({
            onFailed("网络出错啦~请检查网络")
        }, {
            val result = repo.deleteWebsite(id)
            if (result.errorCode == 0) {
                onSuccess()
            } else onFailed(result.errorMsg)
        })
    }
}
