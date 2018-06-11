package com.pudding.tangentninety.view.root.main.storydetail

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil

import java.net.URLEncoder

import javax.inject.Inject

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Error on 2017/6/30 0030.
 */

class StoryDetailPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<StoryDetailContract.View>(), StoryDetailContract.Presenter {

    override val isNightMode: Boolean
        get() = mDataManager.nightModeState

    override val isNoPic: Boolean
        get() = mDataManager.noImageState

    override val isBigFont: Boolean
        get() = mDataManager.bigFontState

    override fun getStory(id: Int) {
        mDataManager.getZhihuDetailByID(id).flatMap {it:ZhihuDetailBean->
            if(it.id!=0)
                Flowable.just(it)
            else  mDataManager.fetchDetailInfo(id)
        }.compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<ZhihuDetailBean>(mView) {
                    override fun onNext(zhihuDetailBean: ZhihuDetailBean) {
                        mView?.showDetail(zhihuDetailBean)
                    }
                }).addSubscribe(this)
    }

    override fun getStoryExtra(id: Int) {
        mDataManager.fetchDetailExtra(id).compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<DetailExtraBean>(mView) {
                    override fun onNext(detailExtraBean: DetailExtraBean) {
                        mView?.showExtra(detailExtraBean)
                    }
                }).addSubscribe(this)
    }

    override fun isImageUrlExist(url: String): Boolean {
        return mDataManager.isImageUrlExist(url)
    }

    override fun addImageUrl(url: String) {
        mDataManager.insertImageUrl(url)
    }

    override fun addStoryToHistory(bean: ZhihuDetailBean) {
        mDataManager.insertHistoryBean(bean)
    }

    override fun isCollectionExist(id: Int){
        mDataManager.isCollectionExist(id).compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<Boolean>(mView){
                    override fun onNext(t: Boolean) {
                        mView?.setCollectionChecked(t)
                    }
                }).addSubscribe(this)
    }

    override fun collectionStory(bean: CollectionStoryBean) {
        mDataManager.insertCollection(bean)
    }

    override fun uncollectionStory(id: Int) {
        mDataManager.removeCollection(id)
    }

    override fun loadDownloadImage(url: String) {
        Flowable.just(url)
                .filter { s -> isImageUrlExist(s) }
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map { s ->
                    val newurl = URLEncoder.encode(s, "GBK")
                    "javascript:img_replace(\"$newurl\",\"$newurl\");"
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CommonSubscriber<String>(mView) {
                    override fun onNext(s: String) {
                        mView?.loadJsUrl(s)
                    }
                })
    }
}
