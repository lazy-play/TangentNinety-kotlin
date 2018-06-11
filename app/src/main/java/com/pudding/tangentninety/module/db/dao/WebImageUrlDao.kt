package com.pudding.tangentninety.module.db.dao

import android.arch.persistence.room.*
import com.pudding.tangentninety.module.bean.WebImageUrlBean


/**
 * This created by Error on 2018/06/05,13:42.
 */
@Dao
interface WebImageUrlDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWebImageUrlBean(bean: WebImageUrlBean)

    @Query("select * FROM WebImageUrlBean WHERE url = :url LIMIT 1")
    fun findWebImageUrlBean(url: String):WebImageUrlBean?

    @Query("delete from WebImageUrlBean")
    fun clearAll();
}