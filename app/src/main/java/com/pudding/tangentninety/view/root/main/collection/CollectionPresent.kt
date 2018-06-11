package com.pudding.tangentninety.view.root.main.collection

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil

import javax.inject.Inject

/**
 * Created by Error on 2017/6/30 0030.
 */

class CollectionPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<CollectionContract.View>(), CollectionContract.Presenter {


    override fun getCollectionList() {
        mDataManager.getCollectionList().compose(RxUtil.rxSchedulerHelper())
                .subscribeWith(object : CommonSubscriber<List<CollectionStoryBean>>(mView){
                    override fun onNext(t: List<CollectionStoryBean>) {
                        println(t)
                        mView?.showCollectionList(t)
                    }
                })
    }

    override fun deleteCollections(ids: MutableList<Int>) {
        mDataManager.removeCollectionList(ids)
    }
}
