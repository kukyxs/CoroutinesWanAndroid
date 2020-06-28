package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */
data class WxChapterData(
    val children: List<Any>,
    val courseId: Int,
    val id: Int,
    val name: String,
    val order: Int,
    val parentChapterId: Int,
    val userControlSetTop: Boolean,
    val visible: Int
)