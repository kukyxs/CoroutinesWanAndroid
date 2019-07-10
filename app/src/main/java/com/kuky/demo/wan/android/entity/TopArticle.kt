package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */

data class TopArticleEntity(
    val `data`: List<ArticleDetail>,
    val errorCode: Int,
    val errorMsg: String
)