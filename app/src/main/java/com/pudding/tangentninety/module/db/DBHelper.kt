package com.pudding.tangentninety.module.db


import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import io.reactivex.Flowable

/**
 * Created by Error on 2017/6/22 0022.
 */

interface DBHelper {
    fun getCollectionList() : Flowable<List<CollectionStoryBean>>
    fun getHistoryBean(): Flowable<List<ZhihuDetailBean>>
    fun insertImageUrl(url: String)
    fun isImageUrlExist(url: String): Boolean
    fun insertHistoryBean(bean: ZhihuDetailBean)
    fun getZhihuDetailByID(id: Int): Flowable<ZhihuDetailBean>
    fun isHistoryExist(id: Int): Boolean
    fun insertCollection(bean: CollectionStoryBean)
    fun removeCollection(id: Int)
    fun isCollectionExist(id: Int): Flowable<Boolean>
    fun removeCollectionList(ids: List<Int>)
    fun insertDownloadStory(id: Int)
    fun isStoryDownload(id: Int): Boolean
    fun clearCache()
}

