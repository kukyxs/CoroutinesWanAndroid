package com.kuky.demo.wan.android.ui.system

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.SystemCategory
import com.kuky.demo.wan.android.entity.SystemData
import com.kuky.demo.wan.android.entity.WxChapterListDatas


/**
 * @author Taonce.
 * @description
 */
class KnowledgeSystemViewModel(private val repository: KnowledgeSystemRepository) : ViewModel() {
    val mType: MutableLiveData<List<SystemData>?> = MutableLiveData()
    var mArticles: LiveData<PagedList<WxChapterListDatas>>? = null
    // 一级体系下标
    val firstSelectedPosition = MutableLiveData<Int>()
    // 二级体系下标
    val secSelectedPosition = MutableLiveData<Int>()
    val children = MutableLiveData<MutableList<SystemCategory>>()

    init {
        firstSelectedPosition.value = 0
        secSelectedPosition.value = 0
        children.value = arrayListOf()
    }

    fun fetchType() {
        viewModelScope.safeLaunch {
            mType.value = repository.loadSystemType()
        }
    }

    fun fetchArticles(cid: Int) {
        mArticles = LivePagedListBuilder(
            KnowledgeSystemDataSourceFactory(repository, cid),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}

