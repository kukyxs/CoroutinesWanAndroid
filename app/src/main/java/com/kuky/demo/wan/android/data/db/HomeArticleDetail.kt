package com.kuky.demo.wan.android.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author kuky.
 * @description
 */

@Entity(tableName = "home_article_cache")
data class HomeArticleDetail(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val cache_id: Long = 0,
    @ColumnInfo(name = "author") val author: String,
    @ColumnInfo(name = "chapter_name") val chapterName: String,
    @ColumnInfo(name = "collect") var collect: Boolean,
    @ColumnInfo(name = "fresh") val fresh: Boolean,
    @ColumnInfo(name = "article_id") val id: Int,
    @ColumnInfo(name = "link") val link: String,
    @ColumnInfo(name = "nice_date") val niceDate: String,
    @ColumnInfo(name = "super_chapter_name") val superChapterName: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "type") val type: Int
)