package com.kuky.demo.wan.android.ui.system

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.entity.SystemCategory
import com.kuky.demo.wan.android.entity.SystemData
import com.kuky.demo.wan.android.entity.WxChapterListDatas
import com.kuky.demo.wan.android.ui.app.constPagerConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


/**
 * @author Taonce.
 * @description
 */
class KnowledgeSystemViewModel(private val repository: KnowledgeSystemRepository) : ViewModel() {
    // 一级体系下标
    val firstSelectedPosition = MutableLiveData<Int>()

    // 二级体系下标
    val secSelectedPosition = MutableLiveData<Int>()
    val children = MutableLiveData<MutableList<SystemCategory>>()

    private var mCurrentCid: Int? = null
    private var mCurrentChaptersResult: Flow<PagingData<WxChapterListDatas>>? = null
    private var mCurrentTypeResult: Flow<MutableList<SystemData>>? = null

    init {
        firstSelectedPosition.value = 0
        secSelectedPosition.value = 0
        children.value = arrayListOf()
    }

    fun getArticles(cid: Int): Flow<PagingData<WxChapterListDatas>> {
        val lastResult = mCurrentChaptersResult
        if (cid == mCurrentCid && lastResult != null) return lastResult

        mCurrentCid = cid
        return Pager(constPagerConfig) {
            KnowledgeSystemPagingSource(repository, cid)
        }.flow.apply {
            mCurrentChaptersResult = this
        }.cachedIn(viewModelScope)
    }

    fun getTypeList(): Flow<MutableList<SystemData>> {
        val lastResult = mCurrentTypeResult
        if (lastResult != null) return lastResult

        return flow {
            emit(repository.loadSystemType())
        }.apply { mCurrentTypeResult = this }
    }
}

