package com.pudding.tangentninety.view.root.main.storydetail.comment

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.StoryCommentsBean
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.RxUtil

import javax.inject.Inject

/**
 * Created by Error on 2017/7/24 0024.
 */

class StoryCommentPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<StoryCommentContract.View>(), StoryCommentContract.Presenter {
    override fun getLongCommentInfo(id: Int) {
                mDataManager.fetchLongCommentInfo(id).compose(RxUtil.rxSchedulerHelper())
                        .subscribeWith(object : CommonSubscriber<StoryCommentsBean>(mView) {

                            override fun onNext(storyCommentsBean: StoryCommentsBean) {
                                mView?.setLongCommentList(storyCommentsBean)
                            }
                        }).addSubscribe(this)
    }

    override fun getLongCommentInfo(id: Int, userid: Int) {
                mDataManager.fetchLongCommentInfo(id, userid).compose(RxUtil.rxSchedulerHelper())
                        .subscribeWith(object : CommonSubscriber<StoryCommentsBean>(mView) {

                            override fun onNext(storyCommentsBean: StoryCommentsBean) {
                                mView?.setLongCommentList(storyCommentsBean)
                            }
                        }).addSubscribe(this)
    }

    override fun getShortCommentInfo(id: Int) {
                mDataManager.fetchShortCommentInfo(id).compose(RxUtil.rxSchedulerHelper())
                        .subscribeWith(object : CommonSubscriber<StoryCommentsBean>(mView) {

                            override fun onNext(storyCommentsBean: StoryCommentsBean) {
                                mView?.setShortCommentList(storyCommentsBean)
                            }
                        }).addSubscribe(this)
    }

    override fun getShortCommentInfo(id: Int, userid: Int) {
                mDataManager.fetchShortCommentInfo(id, userid).compose(RxUtil.rxSchedulerHelper())
                        .subscribeWith(object : CommonSubscriber<StoryCommentsBean>(mView) {
                            override fun onNext(storyCommentsBean: StoryCommentsBean) {
                                mView?.setShortCommentList(storyCommentsBean)
                            }
                        }).addSubscribe(this)
    }

    override fun getStoryExtra(id: Int) {
                mDataManager.fetchDetailExtra(id).compose(RxUtil.rxSchedulerHelper())
                        .subscribeWith(object : CommonSubscriber<DetailExtraBean>(mView) {
                            override fun onNext(detailExtraBean: DetailExtraBean) {
                                    mView?.showExtra(detailExtraBean.comments)
                            }
                        }).addSubscribe(this)
    }
}
