package com.kuky.demo.wan.android.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kuky.demo.wan.android.data.db.HomeArticleDetail

/**
 * @author kuky.
 * @description
 */

@Dao
interface HomeArticleCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun cacheHomeArticles(articles: List<HomeArticleDetail>): List<Long>

    @Query("select * from home_article_cache")
    fun fetchAllCache(): LiveData<List<HomeArticleDetail>>

    @Query("delete from home_article_cache")
    fun clearHomeCache(): Int
}