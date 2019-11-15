package com.kuky.demo.wan.android.entity

import com.kuky.demo.wan.android.data.db.HomeArticleDetail

/**
 * @author kuky.
 * @description
 */

data class TopArticleEntity(
    val `data`: List<HomeArticleDetail>,
    val errorCode: Int,
    val errorMsg: String
)