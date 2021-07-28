package com.kuky.demo.wan.android.ui.coins

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.cachedIn
import com.kuky.demo.wan.android.base.BaseViewModel
import com.kuky.demo.wan.android.ui.app.constPagerConfig

/**
 * @author kuky.
 * @description
 */
class CoinViewModel(
    private val repository: CoinRepository,
    application: Application
) : BaseViewModel(application) {

    suspend fun getCoinRankList() = Pager(constPagerConfig) {
        CoinRankPagingSource(repository)
    }.flow.cachedIn(viewModelScope).doRequest()


    suspend fun getCoinRecordList() = Pager(constPagerConfig) {
        CoinRecordPagingSource(repository)
    }.flow.cachedIn(viewModelScope).doRequest()
}