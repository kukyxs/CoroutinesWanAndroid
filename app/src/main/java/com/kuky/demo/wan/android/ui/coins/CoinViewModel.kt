package com.kuky.demo.wan.android.ui.coins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn

/**
 * @author kuky.
 * @description
 */
class CoinViewModel(private val repository: CoinRepository) : ViewModel() {

    fun getCoinRankList() = repository.getRankStream().cachedIn(viewModelScope)

    fun getCoinRecordList() = repository.getRecordStream().cachedIn(viewModelScope)
}