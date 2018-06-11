package com.pudding.tangentninety.module.http

import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.SectionDetails
import com.pudding.tangentninety.module.bean.SectionListBean
import com.pudding.tangentninety.module.bean.StoryCommentsBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean

import io.reactivex.Flowable

/**
 * Created by Error on 2017/6/22 0022.
 */

interface HttpHelper {

    fun fetchDailyListInfo(): Flowable<DailyNewListBean>
    fun fetchDailyBeforeListInfo(date: String): Flowable<DailyListBean>
    fun fetchDetailInfo(id: Int): Flowable<ZhihuDetailBean>
    fun fetchDetailExtra(id: Int): Flowable<DetailExtraBean>

    fun fetchShortCommentInfo(id: Int): Flowable<StoryCommentsBean>
    fun fetchShortCommentInfo(id: Int, userid: Int): Flowable<StoryCommentsBean>
    fun fetchLongCommentInfo(id: Int): Flowable<StoryCommentsBean>
    fun fetchLongCommentInfo(id: Int, userid: Int): Flowable<StoryCommentsBean>

    fun fetchSectionList(): Flowable<SectionListBean>
    fun fetchSectionDetails(id: Int): Flowable<SectionDetails>
    fun fetchBeforeSectionsDetails(id: Int, timestamp: Long): Flowable<SectionDetails>

    //    Flowable<ResponseBody> getNetFromState();
}