package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */

data class UserCoins(
    val `data`: CoinsData,
    val errorCode: Int,
    val errorMsg: String
)

data class CoinsData(
    val coinCount: Int,
    val level: Int,
    val rank: Int,
    val userId: Int,
    val username: String
)