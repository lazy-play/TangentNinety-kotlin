package com.pudding.tangentninety.module.db.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.pudding.tangentninety.app.App

import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DownloadStory
import com.pudding.tangentninety.module.bean.WebImageUrlBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean

/**
 * This created by Error on 2018/06/05,14:27.
 */
@Database(entities = arrayOf(WebImageUrlBean::class, DownloadStory::class, ZhihuDetailBean::class, CollectionStoryBean::class), version = 2, exportSchema = false)
abstract class RoomDao : RoomDatabase() {
    abstract val webImageUrlDao: WebImageUrlDao
    abstract val downloadStoryDao: DownloadStoryDao
    abstract val collectionStoryDao: CollectionStoryDao
    abstract val zhihuDetailDao: ZhihuDetailDao
    companion object {
        private var instance: RoomDao? = null

        fun getDatabase(): RoomDao {
            if (instance == null) {
                instance = Room.databaseBuilder(App.instance, RoomDao::class.java,
                        "MyRoom.db").build()
            }
            return instance!!
        }

        fun onDestroy() {
            instance = null
        }
    }
}
