package com.kuky.demo.wan.android.ui.collectedwebsites

import android.text.TextUtils
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.base.CODE_SUCCEED
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.WebsiteData
import org.jetbrains.anko.toast

/**
 * @author Taonce.
 * @description
 */
class CollectedWebsitesViewModel(private val repo: CollectedWebsitesRepository) : ViewModel() {
    val mWebsitesData = MutableLiveData<List<WebsiteData>>()

    fun fetchWebSitesData() {
        viewModelScope.safeLaunch {
            mWebsitesData.value = repo.getCollectedWebsites().data
        }
    }

    fun addWebsites(name: String?, link: String?, success: () -> Unit, failed: (msg: String) -> Unit) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(link)) {
            WanApplication.instance.toast("输入不可为空!")
        } else {
            viewModelScope.safeLaunch {
                val result = repo.addWebsite(name!!, link!!)
                if (result.code == CODE_SUCCEED) success()
                else failed(result.message)
            }
        }
    }

    fun deleteWebsite(id: Int) {
        viewModelScope.safeLaunch {
            val result = repo.deleteWebsite(id)
            if (result.errorCode == 0) {
                WanApplication.instance.toast("删除成功")
                fetchWebSitesData()
            } else WanApplication.instance.toast(result.errorMsg)
        }
    }
}
