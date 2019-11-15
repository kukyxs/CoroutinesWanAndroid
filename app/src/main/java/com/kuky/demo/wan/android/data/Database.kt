package com.kuky.demo.wan.android.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.kuky.demo.wan.android.WanApplication
import com.kuky.demo.wan.android.data.db.HomeArticleDetail
import com.kuky.demo.wan.android.utils.LogUtils

/**
 * @author kuky.
 * @description
 */

@Database(entities = [HomeArticleDetail::class], version = 1, exportSchema = false)
abstract class WanDatabase : RoomDatabase() {
    abstract fun homeArticleCacheDao(): HomeArticleCacheDao
}

object WanDatabaseUtils {
    private const val DB_NAME = "wan.db"

    private val instance: WanDatabase by lazy {
        Room.databaseBuilder(WanApplication.instance, WanDatabase::class.java, DB_NAME)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    LogUtils.debug("create database $DB_NAME")
                }

                override fun onOpen(db: SupportSQLiteDatabase) {
                    super.onOpen(db)
                    LogUtils.debug("open database $DB_NAME")
                }
            }).build()
    }

    val homeArticleCacheDao = instance.homeArticleCacheDao()
}