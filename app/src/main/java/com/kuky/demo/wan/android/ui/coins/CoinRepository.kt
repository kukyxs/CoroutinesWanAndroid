package com.kuky.demo.wan.android.ui.coins

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.CoinRankDetail
import com.kuky.demo.wan.android.entity.CoinRecordDetail
import com.kuky.demo.wan.android.network.RetrofitManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CoinRepository {
    private val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)

    suspend fun getCoinRecord(page: Int): List<CoinRecordDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.fetchCoinsRecord(page, cookie).data.datas
        }

    suspend fun getCoinRanks(page: Int): List<CoinRankDetail>? =
        withContext(Dispatchers.IO) {
            RetrofitManager.apiService.fetchCoinRanks(page).data.datas
        }

    fun getRankStream() = Pager(
        config = PagingConfig(pageSize = 20)
    ) { CoinRankPagingSource(this) }.flow

    fun getRecordStream() = Pager(
        config = PagingConfig(pageSize = 20)
    ) { CoinRecordPagingSource(this) }.flow
}