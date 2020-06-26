package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */
data class CoinRecordData(
    val curPage: Int,
    val datas: MutableList<CoinRecordDetail>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class CoinRecordDetail(
    val coinCount: Int,
    val date: Long,
    val desc: String,
    val id: Int,
    val reason: String,
    val type: Int,
    val userId: Int,
    val userName: String
)