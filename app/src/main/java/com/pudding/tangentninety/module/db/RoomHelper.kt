package com.pudding.tangentninety.module.db

import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DownloadStory
import com.pudding.tangentninety.module.bean.WebImageUrlBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.db.dao.RoomDao
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

/**
 * This created by Error on 2018/06/04,16:22.
 */
class RoomHelper @Inject
constructor() : DBHelper {
    override fun getCollectionList(): Flowable<List<CollectionStoryBean>> {
        return Flowable.create({
            it.onNext(RoomDao.getDatabase().collectionStoryDao.findAll())
        },BackpressureStrategy.BUFFER)
    }

    override fun getHistoryBean(): Flowable<List<ZhihuDetailBean>> {
        return Flowable.create({
            it.onNext(RoomDao.getDatabase().zhihuDetailDao.findAll())
        },BackpressureStrategy.BUFFER)
    }

    override fun insertImageUrl(url: String) {
        Flowable.just(url).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    val bean = WebImageUrlBean(it)
                    RoomDao.getDatabase().webImageUrlDao.insertWebImageUrlBean(bean)
                }

    }

    override fun isImageUrlExist(url: String): Boolean {
        return RoomDao.getDatabase().webImageUrlDao.findWebImageUrlBean(url) != null
    }

    override fun insertHistoryBean(bean: ZhihuDetailBean) {
        Flowable.just(bean).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    it.time=System.currentTimeMillis()
                    RoomDao.getDatabase().zhihuDetailDao.insertZhihuDetailBean(it)
                }
    }

    override fun getZhihuDetailByID(id: Int): Flowable<ZhihuDetailBean> {
        return Flowable.just(id).map {
            var bean = RoomDao.getDatabase().zhihuDetailDao.findZhihuDetailBean(it)
            if (bean == null)
                bean = ZhihuDetailBean()
            bean
        }
    }

    override fun isHistoryExist(id: Int): Boolean {
        return RoomDao.getDatabase().zhihuDetailDao.findZhihuDetailBean(id) != null
    }

    override fun insertCollection(bean: CollectionStoryBean) {
        Flowable.just(bean).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    it.time = System.currentTimeMillis()
                    RoomDao.getDatabase().collectionStoryDao.insertCollectionStoryBean(it)
                }
    }

    override fun removeCollection(id: Int) {
        Flowable.just(id).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    RoomDao.getDatabase().collectionStoryDao.deleteCollectionStoryBean(it)
                }
    }

    override fun isCollectionExist(id: Int): Flowable<Boolean> {
        return Flowable.just(id).map {
            RoomDao.getDatabase().collectionStoryDao.findCollectionStoryBean(it) != null
        }
    }

    override fun removeCollectionList(ids: List<Int>) {
        Flowable.just(ids).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    it.forEach { RoomDao.getDatabase().collectionStoryDao.deleteCollectionStoryBean(it) }
                }
    }

    override fun insertDownloadStory(id: Int) {
        Flowable.just(id).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    val bean = DownloadStory(it)
                    RoomDao.getDatabase().downloadStoryDao.insertDownloadStory(bean)
                }
    }

    override fun isStoryDownload(id: Int): Boolean {
        return RoomDao.getDatabase().downloadStoryDao.findDownloadStory(id) != null
    }

    override fun clearCache() {
        Flowable.just(0).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe {
                    RoomDao.getDatabase().downloadStoryDao.clearAll()
                    RoomDao.getDatabase().webImageUrlDao.clearAll()
                }
    }
}
