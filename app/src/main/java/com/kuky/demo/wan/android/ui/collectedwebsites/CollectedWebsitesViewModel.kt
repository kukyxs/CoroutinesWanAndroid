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
    val mWebSitesData = MutableLiveData<List<WebsiteData>>()

    fun fetchWebSitesData() {
        viewModelScope.safeLaunch {
            mWebSitesData.value = repo.getCollectedWebsites().data
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
}
