package com.kuky.demo.wan.android.ui.coins

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.kuky.demo.wan.android.base.PagingThrowableHandler
import com.kuky.demo.wan.android.entity.CoinRankDetail
import com.kuky.demo.wan.android.entity.CoinRecordDetail

/**
 * @author kuky.
 * @description
 */
class CoinViewModel(private val repository: CoinRepository) : ViewModel() {

    var coinRanks: LiveData<PagedList<CoinRankDetail>>? = null
    var coinRecords: LiveData<PagedList<CoinRecordDetail>>? = null

    fun fetchRankList(handler: PagingThrowableHandler) {
        coinRanks = LivePagedListBuilder(
            CoinRankDataSourceFactory(repository, handler),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }

    fun fetchRecordList(handler: PagingThrowableHandler) {
        coinRecords = LivePagedListBuilder(
            CoinRecordDataSourceFactory(repository, handler),
            PagedList.Config.Builder()
                .setPageSize(20)
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(20)
                .build()
        ).build()
    }
}