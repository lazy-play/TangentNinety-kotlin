package com.pudding.tangentninety.module.http

import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.SectionDetails
import com.pudding.tangentninety.module.bean.SectionListBean
import com.pudding.tangentninety.module.bean.StoryCommentsBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.http.api.ZhihuApis

import javax.inject.Inject

import io.reactivex.Flowable

/**
 * Created by Error on 2017/6/22 0022.
 */

class RetrofitHelper @Inject
constructor(private val mZhihuApiService: ZhihuApis) : HttpHelper {


    override fun fetchDailyListInfo(): Flowable<DailyNewListBean> {
        return mZhihuApiService.getDailyList()
    }

    override fun fetchDailyBeforeListInfo(date: String): Flowable<DailyListBean> {
        return mZhihuApiService.getDailyBeforeList(date)
    }


    override fun fetchDetailInfo(id: Int): Flowable<ZhihuDetailBean> {
        return mZhihuApiService.getDetailInfo(id)
    }

    override fun fetchDetailExtra(id: Int): Flowable<DetailExtraBean> {
        return mZhihuApiService.getDetailExtraInfo(id)
    }

    override fun fetchShortCommentInfo(id: Int): Flowable<StoryCommentsBean> {
        return mZhihuApiService.getShortCommentInfo(id)
    }

    override fun fetchShortCommentInfo(id: Int, userid: Int): Flowable<StoryCommentsBean> {
        return mZhihuApiService.getShortCommentInfo(id, userid)
    }

    override fun fetchLongCommentInfo(id: Int): Flowable<StoryCommentsBean> {
        return mZhihuApiService.getLongCommentInfo(id)
    }

    override fun fetchLongCommentInfo(id: Int, userid: Int): Flowable<StoryCommentsBean> {
        return mZhihuApiService.getLongCommentInfo(id, userid)
    }

    override fun fetchSectionList(): Flowable<SectionListBean> {
        return mZhihuApiService.getSectionList()
    }

    override fun fetchSectionDetails(id: Int): Flowable<SectionDetails> {
        return mZhihuApiService.getSectionDetails(id)
    }

    override fun fetchBeforeSectionsDetails(id: Int, timestamp: Long): Flowable<SectionDetails> {
        return mZhihuApiService.getBeforeSectionsDetails(id, timestamp)
    }

    //    @Override
    //    public Flowable<ResponseBody> getNetFromState() {
    //        return mZhihuApiService.getNetFromState();
    //    }
}