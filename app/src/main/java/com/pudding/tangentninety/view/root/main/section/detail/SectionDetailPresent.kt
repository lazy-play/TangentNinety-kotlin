package com.pudding.tangentninety.view.root.main.section.detail

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.SectionDetails
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil

import javax.inject.Inject

/**
 * Created by Error on 2017/6/30 0030.
 */

class SectionDetailPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<SectionDetailContract.View>(), SectionDetailContract.Presenter {

    override val isNoPic: Boolean
        get() = mDataManager.noImageState


    override fun getSectionDetail(id: Int) {
        mDataManager.fetchSectionDetails(id).compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<SectionDetails>(mView) {
                    override fun onNext(sectionDetails: SectionDetails) {
                        mView?.showResult(sectionDetails)
                    }
                }).addSubscribe(this)
    }

    override fun getSectionDetailBefore(id: Int, timeBefore: Long) {
        mDataManager.fetchBeforeSectionsDetails(id, timeBefore).compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<SectionDetails>(mView) {
                    override fun onNext(sectionDetails: SectionDetails) {
                        mView?.showResult(sectionDetails)
                    }
                }).addSubscribe(this)
    }

}
