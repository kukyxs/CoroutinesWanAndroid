package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */
data class SystemData(
    val children: List<SystemCategory>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)

data class SystemCategory(
    val children: List<Any>,
    val courseId: Int,
    val id: Int, // id 用于查询体系下的文章
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)