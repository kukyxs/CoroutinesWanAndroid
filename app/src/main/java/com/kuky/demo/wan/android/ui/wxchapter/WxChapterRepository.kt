package com.kuky.demo.wan.android.ui.wxchapter

import com.kuky.demo.wan.android.network.ApiService
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class WxChapterRepository(private val api: ApiService) {
    suspend fun getWxChapter() = withContext(Dispatchers.IO) {
        api.wxCahpters().data
    }
}