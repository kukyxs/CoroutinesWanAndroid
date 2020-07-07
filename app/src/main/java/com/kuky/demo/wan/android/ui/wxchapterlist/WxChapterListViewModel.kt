package com.kuky.demo.wan.android.ui.wxchapterlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.Flow


/**
 * @author kuky.
 * @description
 */
class WxChapterListViewModel(private val repository: WxChapterListRepository) : ViewModel() {

    private var mCurrentWxId: Int? = null
    private var mCurrentKeyword: String? = null
    private var mCurrentChapterResult: Flow<PagingData<WxChapterListDatas>>? = null

    fun getWxChapters(wxId: Int, keyword: String): Flow<PagingData<WxChapterListDatas>> {
        val lastResult = mCurrentChapterResult
        if (mCurrentWxId == wxId && mCurrentKeyword == keyword && lastResult != null) return lastResult

        mCurrentWxId = wxId
        mCurrentKeyword = keyword

        return Pager(constPagerConfig) {
            WxChapterListPagingSource(repository, wxId, keyword)
        }.flow.apply {
            mCurrentChapterResult = this
        }.cachedIn(viewModelScope)
    }
}

