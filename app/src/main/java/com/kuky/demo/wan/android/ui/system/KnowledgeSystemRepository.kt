package com.kuky.demo.wan.android.ui.system

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.network.RetrofitManager
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
            RetrofitManager.apiService.articleInCategory(
                page, cid, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data.datas
        }
}