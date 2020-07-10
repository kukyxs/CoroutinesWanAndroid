package com.kuky.demo.wan.android.ui.wxchapterlist

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class WxChapterListRepository(private val api: ApiService) {

    suspend fun loadPage(wxId: Int, page: Int, key: String): MutableList<WxChapterListDatas>? =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.wxChapterList(wxId, page, cookie, key).data.datas
        }
}