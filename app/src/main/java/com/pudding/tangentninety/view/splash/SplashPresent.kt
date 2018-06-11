package com.pudding.tangentninety.view.splash

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil

import javax.inject.Inject


/**
 * Created by Error on 2017/6/30 0030.
 */

class SplashPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<SplashContract.View>(), SplashContract.Presenter {
    override fun loadDailyNewList() {
        mDataManager.fetchDailyListInfo().compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<DailyNewListBean>(mView){
                    override fun onNext(bean: DailyNewListBean?) {
                        mView?.loadDailyNewSuccess(bean)
                    }
                }).addSubscribe(this)
    }
}
