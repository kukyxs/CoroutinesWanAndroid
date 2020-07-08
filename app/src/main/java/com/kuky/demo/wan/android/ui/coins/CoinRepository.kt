package com.kuky.demo.wan.android.ui.coins

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.CoinRankData
import com.kuky.demo.wan.android.entity.CoinRecordData
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CoinRepository(private val api: ApiService) {

    suspend fun getCoinRecord(page: Int): CoinRecordData =
        withContext(Dispatchers.IO) {
            val cookie = PreferencesHelper.fetchCookie(WanApplication.instance)
            api.fetchCoinsRecord(page, cookie).data
        }

    suspend fun getCoinRanks(page: Int): CoinRankData = withContext(Dispatchers.IO) {
        api.fetchCoinRanks(page).data
    }
}