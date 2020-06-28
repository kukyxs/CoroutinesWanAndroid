package com.kuky.demo.wan.android.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.data.db.HomeArticleRemoteKey

/**
 * @author kuky.
 * @description
 */

@Dao
interface HomeArticleCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheHomeArticles(articles: List<HomeArticleDetail>)

    @Query("select * from home_article_cache")
    fun fetchAllHomeArticleCache(): PagingSource<Int, HomeArticleDetail>

    @Query("delete from home_article_cache")
    suspend fun clearHomeArticleCache()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun cacheHomeArtRemoteKey(remoteKey: List<HomeArticleRemoteKey>)

    @Query("select * from home_article_remote_key where article_id = :artId")
    suspend fun remoteKeyByArtId(artId: Int): HomeArticleRemoteKey?

    @Query("delete from home_article_remote_key")
    suspend fun clearHomeArtRemoteKey()
}