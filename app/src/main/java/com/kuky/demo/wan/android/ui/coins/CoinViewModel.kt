package com.kuky.demo.wan.android.ui.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.ui.app.constPagerConfig

/**
 * @author kuky.
 * @description
 */
class CoinViewModel(private val repository: CoinRepository) : ViewModel() {

    fun getCoinRankList() = Pager(constPagerConfig) {
        CoinRankPagingSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun getCoinRecordList() = Pager(constPagerConfig) {
        CoinRecordPagingSource(repository)
    }.flow.cachedIn(viewModelScope)
}