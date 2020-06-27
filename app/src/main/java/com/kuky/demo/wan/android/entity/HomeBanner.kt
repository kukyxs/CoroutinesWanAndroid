package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */
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