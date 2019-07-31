package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */
data class WebsiteEntity(
    val `data`: List<WebsiteData>?,
    val errorCode: Int,
    val errorMsg: String
)

data class WebsiteData(
    val icon: String,
    val id: Int,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)

data class AddWebsiteData(
    val `data`: WebsiteData,
    val errorCode: Int,
    val errorMsg: String
)