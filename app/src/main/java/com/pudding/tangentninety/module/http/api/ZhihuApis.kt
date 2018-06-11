package com.pudding.tangentninety.module.http.api

import com.pudding.tangentninety.app.Constants
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.SectionDetails
import com.pudding.tangentninety.module.bean.SectionListBean
import com.pudding.tangentninety.module.bean.StoryCommentsBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

/**
 * Created by Error on 2017/6/22 0022.
 */

interface ZhihuApis {
    /**
     * 最新日报
     */
    @Headers(NET_CACHE_STRING)
    @GET("news/latest")
    fun getDailyList(): Flowable<DailyNewListBean>
    /**
     * 专栏日报
     */
    @Headers(NET_CACHE_STRING)
    @GET("sections")
    fun getSectionList():Flowable<SectionListBean>

    /**
     * 往期日报
     */
    @Headers(NO_NET_CACHE_STRING)
    @GET("news/before/{date}")
    fun getDailyBeforeList(@Path("date") date: String): Flowable<DailyListBean>


    /**
     * 日报详情
     */
    @Headers(NO_NET_CACHE_STRING)
    @GET("news/{id}")
    fun getDetailInfo(@Path("id") id: Int): Flowable<ZhihuDetailBean>

    /**
     * 日报的额外信息
     */
    @Headers(NET_CACHE_STRING)
    @GET("story-extra/{id}")
    fun getDetailExtraInfo(@Path("id") id: Int): Flowable<DetailExtraBean>

    /**
     * 日报的长评论
     */
    @Headers(NET_CACHE_STRING)
    @GET("story/{id}/long-comments")
    fun getLongCommentInfo(@Path("id") id: Int): Flowable<StoryCommentsBean>

    /**
     * 日报某条长评论之前的长评论
     */
    @Headers(NET_CACHE_STRING)
    @GET("story/{id}/long-comments/before/{userid}")
    fun getLongCommentInfo(@Path("id") id: Int, @Path("userid") userid: Int): Flowable<StoryCommentsBean>

    /**
     * 日报的短评论
     */
    @Headers(NET_CACHE_STRING)
    @GET("story/{id}/short-comments")
    fun getShortCommentInfo(@Path("id") id: Int): Flowable<StoryCommentsBean>

    /**
     * 日报某条短评论之前的短评论
     */
    @Headers(NET_CACHE_STRING)
    @GET("story/{id}/short-comments/before/{userid}")
    fun getShortCommentInfo(@Path("id") id: Int, @Path("userid") userid: Int): Flowable<StoryCommentsBean>

    /**
     * 专栏日报详情
     */
    @Headers(NET_CACHE_STRING)
    @GET("section/{id}")
    fun getSectionDetails(@Path("id") id: Int): Flowable<SectionDetails>

    /**
     * 获取专栏的之前消息
     */
    @Headers(NET_CACHE_STRING)
    @GET("section/{id}/before/{timestamp}")
    fun getBeforeSectionsDetails(@Path("id") id: Int, @Path("timestamp") timestamp: Long): Flowable<SectionDetails>

    companion object {
        //根地址
        const val HOST = "https://news-at.zhihu.com/api/4/"

        //永远联网请求最新
        const val NET_CACHE_STRING = "Cache-Control:public, max-age=${Constants.NET_CACHE_TIME}"
        //只使用缓存
        const val NO_NET_CACHE_STRING = "Cache-Control:public, max-stale=${Constants.NO_NET_CACHE_TIME}"
    }

    //    @Headers(NET_CACHE_STRING)
    //    @GET("http://ip.chinaz.com/getip.aspx")
    //    Flowable<ResponseBody> getNetFromState();

}
