package com.pudding.tangentninety.view.root.main.history

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil

import javax.inject.Inject

/**
 * Created by Error on 2017/6/30 0030.
 */

class HistoryPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<HistoryContract.View>(), HistoryContract.Presenter {


    override fun getHistoryList() {
        mDataManager.getHistoryBean().compose(RxUtil.rxSchedulerHelper())
                .subscribe(object : CommonSubscriber<List<ZhihuDetailBean>>(mView){
                    override fun onNext(t: List<ZhihuDetailBean>) {
                        mView?.showHistoryList(t)
                    }

                })
    }
}
