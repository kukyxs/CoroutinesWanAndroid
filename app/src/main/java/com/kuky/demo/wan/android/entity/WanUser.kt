package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */
data class WanUserData(
    val admin: Boolean,
    val chapterTops: List<Any>,
    val collectIds: List<Any>,
    val email: String,
    val icon: String,
    val id: Int,
    val nickname: String,
    val password: String,
    val token: String,
    val type: Int,
    val username: String
)