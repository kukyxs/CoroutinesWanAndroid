package com.kuky.demo.wan.android.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.data.db.HomeArticleRemoteKey

/**
 * @author kuky.
 * @description
 */

@Database(
    entities = [HomeArticleDetail::class, HomeArticleRemoteKey::class],
    version = 1, exportSchema = false
)
abstract class WanDatabase : RoomDatabase() {
    abstract fun homeArticleCacheDao(): HomeArticleCacheDao

    companion object {
        @Volatile
        private var INSTANCE: WanDatabase? = null

        fun getInstance(context: Context): WanDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                WanDatabase::class.java, "wan.db"
            ).build()
    }
}