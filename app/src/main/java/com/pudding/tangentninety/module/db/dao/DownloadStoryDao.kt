package com.pudding.tangentninety.module.db.dao

import android.arch.persistence.room.*
import com.pudding.tangentninety.module.bean.DownloadStory


/**
 * This created by Error on 2018/06/05,13:42.
 */
@Dao
interface DownloadStoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDownloadStory(bean: DownloadStory)

    @Query("select * FROM DownloadStory WHERE id = :id LIMIT 1")
    fun findDownloadStory(id: Int):DownloadStory?

    @Query("delete from DownloadStory")
    fun clearAll();
}