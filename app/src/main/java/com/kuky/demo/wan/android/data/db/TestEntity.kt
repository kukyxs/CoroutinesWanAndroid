package com.kuky.demo.wan.android.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author kuky.
 * @description
 */
@Entity(tableName = "test")
class TestEntity(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    @ColumnInfo(name = "name") var name: String = ""
)