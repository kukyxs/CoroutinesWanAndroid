package com.kuky.demo.wan.android.data.wxchapter

import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author Taonce.
 * @description
 */

class WxChapterRepository{
    suspend fun getWxChapter() = withContext(Dispatchers.IO){
        val result = RetrofitManager.apiService.wxCahpters()
        result
    }
}