package com.kuky.demo.wan.android.ui.coins

import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.PreferencesHelper
import com.kuky.demo.wan.android.entity.CoinRankDetail
import com.kuky.demo.wan.android.entity.CoinRecordDetail
import com.kuky.demo.wan.android.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class CoinRepository(private val api: ApiService) {

    suspend fun getCoinRecord(page: Int): MutableList<CoinRecordDetail>? =
        withContext(Dispatchers.IO) {
            api.fetchCoinsRecord(
                page, PreferencesHelper.fetchCookie(WanApplication.instance)
            ).data.datas
        }

    suspend fun getCoinRanks(page: Int): MutableList<CoinRankDetail>? =
        withContext(Dispatchers.IO) {
            api.fetchCoinRanks(page).data.datas
        }
}