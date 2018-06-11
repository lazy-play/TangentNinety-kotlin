package com.pudding.tangentninety.module.db.dao

import android.arch.persistence.room.*
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DownloadStory


/**
 * This created by Error on 2018/06/05,13:42.
 */
@Dao
interface CollectionStoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCollectionStoryBean(bean: CollectionStoryBean)

    @Query("delete from CollectionStoryBean where id=:id")
    fun deleteCollectionStoryBean(id:Int)

    @Query("select * FROM CollectionStoryBean WHERE id = :id LIMIT 1")
    fun findCollectionStoryBean(id: Int):CollectionStoryBean?

    @Query("select * FROM CollectionStoryBean order by time desc")
    fun findAll():List<CollectionStoryBean>
}