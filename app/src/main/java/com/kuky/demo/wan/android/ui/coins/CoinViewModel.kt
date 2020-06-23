package com.kuky.demo.wan.android.ui.coins

import androidx.lifecycle.ViewModel
import androidx.paging.Pager
import androidx.paging.PagingConfig

/**
 * @author kuky.
 * @description
 */
class CoinViewModel(private val repository: CoinRepository) : ViewModel() {
    val coinRankList = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = true, prefetchDistance = 5)
    ) { CoinRankPagingSource(repository) }.flow

    val coinRecordList = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = true, prefetchDistance = 5)
    ) { CoinRecordPagingSource(repository) }.flow
}