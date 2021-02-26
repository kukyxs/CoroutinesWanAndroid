package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */

data class FriendWebsite(
    val id: Int,
    val category: String,
    val icon: String,
    val link: String,
    val name: String,
    val order: Int,
    val visible: Int
)