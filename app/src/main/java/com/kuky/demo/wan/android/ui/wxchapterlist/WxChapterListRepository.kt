package com.kuky.demo.wan.android.ui.wxchapterlist

import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class WxChapterListRepository {
    suspend fun loadPage(wxId: Int, page: Int, key: String): MutableList<WxChapterListDatas>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.wxChapterList(wxId, page, cookie, key).data.datas
        }
}