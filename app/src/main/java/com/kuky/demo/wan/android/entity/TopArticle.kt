package com.kuky.demo.wan.android.entity

/**
 * @author kuky.
 * @description
 */

data class TopArticleEntity(
    val `data`: List<TopArticleData>,
    val errorCode: Int,
    val errorMsg: String
)

data class TopArticleData(
    val apkLink: String,
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val collect: Boolean,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val fresh: Boolean,
    val id: Int,
    val link: String,
    val niceDate: String,
    val origin: String,
    val prefix: String,
    val projectLink: String,
    val publishTime: Long,
    val superChapterId: Int,
    val superChapterName: String,
    val tags: List<ArticleTag>,
    val title: String,
    val type: Int,
    val userId: Int,
    val visible: Int,
    val zan: Int
)

data class ArticleTag(
    val name: String,
    val url: String
)