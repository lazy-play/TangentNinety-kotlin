package com.pudding.tangentninety.view.root.main.home

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil
import io.reactivex.Flowable

import javax.inject.Inject

/**
 * Created by Error on 2017/7/3 0003.
 */

class HomePresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<HomeContract.View>(), HomeContract.Presenter {

    override val isNoPic: Boolean
        get() = mDataManager.noImageState

    override fun loadDailyNewList() {
        mDataManager.fetchDailyListInfo().map{
            if(it.date==null)
                throw RuntimeException("the end.")
            it.stories?.forEach{bean->
                bean.isHasRead = mDataManager.isHistoryExist(bean.id)
            }
            it
        }.compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<DailyNewListBean>(mView) {
                    override fun onNext(dailyNewListBean: DailyNewListBean) {
                        mView?.showDailyNewList(dailyNewListBean, null)
                    }
                }).addSubscribe(this)
    }

    override fun loadDailyBeforeList(before: String) {
        mDataManager.fetchDailyBeforeListInfo(before).map{
            if(it.date==null)
                throw RuntimeException("the end.")
            it.stories?.forEach{bean->
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
}
