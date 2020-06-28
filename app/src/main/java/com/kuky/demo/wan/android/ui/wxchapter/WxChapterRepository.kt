package com.kuky.demo.wan.android.ui.wxchapter

import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class WxChapterRepository {
    suspend fun getWxChapter() = withContext(Dispatchers.IO) {
        RetrofitManager.apiService.wxCahpters().data
    }
}