package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */
data class CoinRankData(
    val curPage: Int,
    val datas: MutableList<CoinRankDetail>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class CoinRankDetail(
    val coinCount: Int,
    val level: Int,
    val rank: Int,
    val userId: Int,
    val username: String
)