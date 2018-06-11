package com.pudding.tangentninety.view.root.main.download

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.ImageLoader
import com.pudding.tangentninety.utils.RxUtil

import javax.inject.Inject

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

/**
 * Created by Error on 2017/6/28 0028.
 */

class DownloadPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<DownloadContract.View>(), DownloadContract.Presenter {

    override fun loadDailyNewList() {
        mDataManager.fetchDailyListInfo().map {
            if(it.date==null)
                throw RuntimeException("the end.")
            it.top_stories?.forEach { bean ->
                if (isStoryDownload(bean.id))
                    (it.top_stories as MutableList).remove(bean)
            }
            it.stories?.forEach { bean ->
                if (isStoryDownload(bean.id))
                    (it.stories as MutableList).remove(bean)
                else
                    bean.isHasRead = mDataManager.isHistoryExist(bean.id)
            }
            it
        }.compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<DailyNewListBean>(mView) {
                    override fun onNext(dailyNewListBean: DailyNewListBean) {
                        mView?.showDailyNewList(dailyNewListBean)
                    }
                }
                ).addSubscribe(this)
    }

    override fun loadDailyBeforeList(before: String) {
        mDataManager.fetchDailyBeforeListInfo(before).map {
            if(it.date==null)
                throw RuntimeException("the end.")
            it.stories?.forEach { bean ->
                if (isStoryDownload(bean.id))
                    (it.stories as MutableList).remove(bean)
                else
                bean.isHasRead = mDataManager.isHistoryExist(bean.id)
            }
            it
        }.compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<DailyListBean>(mView) {
                    override fun onNext(dailyListBean: DailyListBean) {
                        mView?.showDailyBeforeList(dailyListBean)
                    }
                }).addSubscribe(this)
    }

    override fun getStory(id: Int) {
        mDataManager.fetchDetailInfo(id).compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<ZhihuDetailBean>(mView) {
                    override fun onNext(zhihuDetailBean: ZhihuDetailBean) {
                        mView?.showDetail(zhihuDetailBean)
                    }
                }).addSubscribe(this)
    }

    override fun addDownloadStory(id: Int) {
        mDataManager.insertDownloadStory(id)
    }

    override fun isStoryDownload(id: Int): Boolean {
        return mDataManager.isStoryDownload(id)
    }


    override fun addImageUrl(url: String) {
        Flowable.just(url)
                .map {
                    ImageLoader.getGlideCache(it)
                    it
                }.compose(RxUtil.rxSchedulerHelper())
                .subscribe(object : CommonSubscriber<String>(mView) {
                    override fun onNext(s: String) {
                        mDataManager.insertImageUrl(s)
                    }
                })

    }

    override fun loadImageUrl(url: String) {
        Flowable.just(url).observeOn(Schedulers.io())
                .subscribe(object : CommonSubscriber<String>(mView) {
                    override fun onNext(s: String) {
                        ImageLoader.getGlideCache(s)
                    }
                })
    }
}
