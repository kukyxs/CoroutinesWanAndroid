package com.kuky.demo.wan.android.entity

import java.io.Serializable

/**
 * @author kuky.
 * @description
 */
data class TodoEntity(
    val `data`: TodoData,
    val errorCode: Int,
    val errorMsg: String
)

data class TodoData(
    val curPage: Int,
    val datas: MutableList<TodoInfo>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)

data class TodoInfo(
    val completeDate: Long?,
    val completeDateStr: String,
    val content: String,
    val date: Long,
    val dateStr: String,
    val id: Int,
    val priority: Int,
    val status: Int,
    val title: String,
    val type: Int,
    val userId: Int
) : Serializable