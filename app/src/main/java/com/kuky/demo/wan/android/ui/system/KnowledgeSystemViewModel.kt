package com.kuky.demo.wan.android.ui.system

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.base.safeLaunch
import com.kuky.demo.wan.android.entity.SystemCategory
import com.kuky.demo.wan.android.entity.SystemData
import com.kuky.demo.wan.android.entity.WxChapterListDatas


/**
 * @author Taonce.
 * @description
 */
class KnowledgeSystemViewModel(private val repository: KnowledgeSystemRepository) : ViewModel() {

    val typeNetState = MutableLiveData<NetworkState>()
    var netState: LiveData<NetworkState>? = null

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
        viewModelScope.safeLaunch({
            typeNetState.postValue(NetworkState.LOADING)
            mType.value = repository.loadSystemType()
            typeNetState.postValue(NetworkState.LOADED)
        }, { typeNetState.postValue(NetworkState.error(it.message)) })
    }

    fun fetchArticles(cid: Int, empty: () -> Unit) {
        mArticles = LivePagedListBuilder(
            KnowledgeSystemDataSourceFactory(repository, cid).apply {
                netState = Transformations.switchMap(sourceLiveData) { it.initState }
            }, PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<WxChapterListDatas>() {
            override fun onZeroItemsLoaded() = empty()
        }).build()
    }
}

