package com.kuky.demo.wan.android.ui.collectedwebsites

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.WebsiteData

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesViewModel(private val repo: CollectedWebsitesRepository) : ViewModel() {
    val mWebsitesData = MutableLiveData<List<WebsiteData>?>()

    fun fetchWebSitesData() {
        viewModelScope.safeLaunch {
            mWebsitesData.value = repo.getCollectedWebsites()
        }
    }

    fun addWebsites(
        name: String?,
        link: String?,
        success: () -> Unit,
        failed: (msg: String, isDismiss: Boolean) -> Unit
    ) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(link)) {
            failed("输入不可为空!", false)
        } else {
            viewModelScope.safeLaunch {
                val result = repo.addWebsite(name!!, link!!)
                if (result.code == CODE_SUCCEED) success()
                else failed(result.message, true)
            }
        }
    }

    fun deleteWebsite(id: Int, onSuccess: () -> Unit, onFailed: (errorMsg: String) -> Unit) {
        viewModelScope.safeLaunch {
            val result = repo.deleteWebsite(id)
            if (result.errorCode == 0) {
                onSuccess()
            } else onFailed(result.errorMsg)
        }
    }
}
