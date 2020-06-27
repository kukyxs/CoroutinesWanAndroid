package com.kuky.demo.wan.android.ui.system

import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.network.RetrofitManager
import com.kuky.demo.wan.android.ui.app.cookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class KnowledgeSystemRepository {
    suspend fun loadSystemType() =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.knowledgeSystem().data
        }

    suspend fun loadArticle4System(page: Int, cid: Int): MutableList<WxChapterListDatas>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.articleInCategory(page, cid, cookie).data.datas
        }
}