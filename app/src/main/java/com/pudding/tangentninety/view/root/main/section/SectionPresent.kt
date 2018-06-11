package com.pudding.tangentninety.view.root.main.section

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.SectionListBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil

import javax.inject.Inject

/**
 * Created by Error on 2017/6/30 0030.
 */

class SectionPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<SectionContract.View>(), SectionContract.Presenter {


    override fun getSectionList() {
                mDataManager.fetchSectionList().compose(RxUtil.rxSchedulerHelper())
                        .subscribeWith(object : CommonSubscriber<SectionListBean>(mView) {
                            override fun onNext(sectionListBean: SectionListBean) {
                                    mView?.showSectionList(sectionListBean)
                            }
                        }).addSubscribe(this)
    }
}
