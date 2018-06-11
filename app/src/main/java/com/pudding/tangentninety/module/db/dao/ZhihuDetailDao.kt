package com.pudding.tangentninety.module.db.dao

import android.arch.persistence.room.*
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DownloadStory
import com.pudding.tangentninety.module.bean.ZhihuDetailBean


/**
 * This created by Error on 2018/06/05,13:42.
 */
@Dao
interface ZhihuDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertZhihuDetailBean(bean: ZhihuDetailBean)

    @Query("select * FROM ZhihuDetailBean WHERE id = :id LIMIT 1")
    fun findZhihuDetailBean(id: Int):ZhihuDetailBean?

    @Query("select * FROM ZhihuDetailBean order by time desc")
    fun findAll():List<ZhihuDetailBean>
}