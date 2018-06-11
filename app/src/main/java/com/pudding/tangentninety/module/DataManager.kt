package com.pudding.tangentninety.module

import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.SectionDetails
import com.pudding.tangentninety.module.bean.SectionListBean
import com.pudding.tangentninety.module.bean.StoryCommentsBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.db.DBHelper
import com.pudding.tangentninety.module.http.HttpHelper
import com.pudding.tangentninety.module.prefs.PreferencesHelper

import io.reactivex.Flowable

/**
 * Created by Error on 2017/6/22 0022.
 */

class DataManager(private val mHttpHelper: HttpHelper, private val mDbHelper: DBHelper, private val mPreferencesHelper: PreferencesHelper) : HttpHelper, DBHelper, PreferencesHelper {
    override fun getCollectionList(): Flowable<List<CollectionStoryBean>> {
        return mDbHelper.getCollectionList()
    }

    override fun getHistoryBean(): Flowable<List<ZhihuDetailBean>> {
        return mDbHelper.getHistoryBean()
    }

    override var nightModeState: Boolean
        get() = mPreferencesHelper.nightModeState
        set(state) {
            mPreferencesHelper.nightModeState = state
        }

    override var noImageState: Boolean
        get() = mPreferencesHelper.noImageState
        set(state) {
            mPreferencesHelper.noImageState = state
        }

    override var autoCacheState: Boolean
        get() = mPreferencesHelper.autoCacheState
        set(state) {
            mPreferencesHelper.autoCacheState = state
        }

    override var bigFontState: Boolean
        get() = mPreferencesHelper.bigFontState
        set(state) {
            mPreferencesHelper.bigFontState = state
        }


    override fun insertImageUrl(url: String) {
        mDbHelper.insertImageUrl(url)
    }

    override fun isImageUrlExist(url: String): Boolean {
        return mDbHelper.isImageUrlExist(url)
    }

    override fun insertHistoryBean(bean: ZhihuDetailBean) {
        mDbHelper.insertHistoryBean(bean)
    }

    override fun getZhihuDetailByID(id: Int): Flowable<ZhihuDetailBean> {
        return mDbHelper.getZhihuDetailByID(id)
    }

    override fun isHistoryExist(id: Int): Boolean {
        return mDbHelper.isHistoryExist(id)
    }

    override fun insertCollection(bean: CollectionStoryBean) {
        mDbHelper.insertCollection(bean)
    }

    override fun removeCollection(id: Int) {
        mDbHelper.removeCollection(id)
    }

    override fun isCollectionExist(id: Int): Flowable<Boolean>  {
        return mDbHelper.isCollectionExist(id)
    }

    override fun removeCollectionList(ids: List<Int>) {
        mDbHelper.removeCollectionList(ids)
    }

    override fun insertDownloadStory(id: Int) {
        mDbHelper.insertDownloadStory(id)
    }

    override fun isStoryDownload(id: Int): Boolean {
        return mDbHelper.isStoryDownload(id)
    }

    override fun clearCache() {
        mDbHelper.clearCache()
    }

    override fun fetchDailyListInfo(): Flowable<DailyNewListBean> {
        return mHttpHelper.fetchDailyListInfo()
    }

    override fun fetchDailyBeforeListInfo(date: String): Flowable<DailyListBean> {
        return mHttpHelper.fetchDailyBeforeListInfo(date)
    }

    override fun fetchDetailInfo(id: Int): Flowable<ZhihuDetailBean> {
        return mHttpHelper.fetchDetailInfo(id)
    }

    override fun fetchDetailExtra(id: Int): Flowable<DetailExtraBean> {
        return mHttpHelper.fetchDetailExtra(id)
    }

    override fun fetchShortCommentInfo(id: Int): Flowable<StoryCommentsBean> {
        return mHttpHelper.fetchShortCommentInfo(id)
    }

    override fun fetchShortCommentInfo(id: Int, userid: Int): Flowable<StoryCommentsBean> {
        return mHttpHelper.fetchShortCommentInfo(id, userid)
    }

    override fun fetchLongCommentInfo(id: Int): Flowable<StoryCommentsBean> {
        return mHttpHelper.fetchLongCommentInfo(id)
    }

    override fun fetchLongCommentInfo(id: Int, userid: Int): Flowable<StoryCommentsBean> {
        return mHttpHelper.fetchLongCommentInfo(id, userid)
    }

    override fun fetchSectionList(): Flowable<SectionListBean> {
        return mHttpHelper.fetchSectionList()
    }

    override fun fetchSectionDetails(id: Int): Flowable<SectionDetails> {
        return mHttpHelper.fetchSectionDetails(id)
    }

    override fun fetchBeforeSectionsDetails(id: Int, timestamp: Long): Flowable<SectionDetails> {
        return mHttpHelper.fetchBeforeSectionsDetails(id, timestamp)
    }

    //    @Override
    //    public Flowable<ResponseBody> getNetFromState() {
    //        return mHttpHelper.getNetFromState();
    //    }
}

