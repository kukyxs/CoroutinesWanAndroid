package com.kuky.demo.wan.android.ui.coins

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.NetworkState
import com.kuky.demo.wan.android.entity.CoinRankDetail
import com.kuky.demo.wan.android.entity.CoinRecordDetail

/**
 * @author kuky.
 * @description
 */
class CoinViewModel(private val repository: CoinRepository) : ViewModel() {

    var rankNetState: LiveData<NetworkState>? = null
    var recordNetState: LiveData<NetworkState>? = null

    var coinRanks: LiveData<PagedList<CoinRankDetail>>? = null
    var coinRecords: LiveData<PagedList<CoinRecordDetail>>? = null

    fun fetchRankList(empty: () -> Unit) {
        coinRanks = LivePagedListBuilder(
            CoinRankDataSourceFactory(repository).apply {
                rankNetState = Transformations.switchMap(sourceLiveData) { it.initState }
            }, PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<CoinRankDetail>() {
            override fun onZeroItemsLoaded() = empty()
        }).build()
    }

    fun fetchRecordList(empty: () -> Unit) {
        coinRecords = LivePagedListBuilder(
            CoinRecordDataSourceFactory(repository).apply {
                recordNetState = Transformations.switchMap(sourceLiveData) { it.initState }
            }, PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).setBoundaryCallback(object : PagedList.BoundaryCallback<CoinRecordDetail>() {
            override fun onZeroItemsLoaded() = empty()
        }).build()
    }
}