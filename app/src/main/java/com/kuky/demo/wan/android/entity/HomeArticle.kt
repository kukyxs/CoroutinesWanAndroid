package com.kuky.demo.wan.android.entity

import com.kuky.demo.wan.android.data.db.HomeArticleDetail

/**
 * @author kuky.
 * @description
 */
data class ArticleData(
    val curPage: Int,
    val datas: MutableList<HomeArticleDetail>,
    val offset: Int,
    val over: Boolean,
    val pageCount: Int,
    val size: Int,
    val total: Int
)