package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */

data class HomeBannerEntity(
    val `data`: List<BannerData>,
    val errorCode: Int,
    val errorMsg: String
)

data class BannerData(
    val desc: String,
    val id: Int,
    val imagePath: String,
    val isVisible: Int,
    val order: Int,
    val title: String,
    val type: Int,
    val url: String
)