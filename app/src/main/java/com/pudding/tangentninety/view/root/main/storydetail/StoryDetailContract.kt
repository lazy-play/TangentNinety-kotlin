package com.pudding.tangentninety.view.root.main.storydetail

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean

/**
 * Created by Error on 2017/6/28 0028.
 */

interface StoryDetailContract {
    interface View : BaseView {
        fun showDetail(zhihuDetailBean: ZhihuDetailBean)
        fun showExtra(detailExtraBean: DetailExtraBean)
        fun setCollectionChecked(checked: Boolean)
        fun loadJsUrl(url: String)
    }

    interface Presenter : BasePresenter<View> {
        val isNightMode: Boolean
        val isNoPic: Boolean
        val isBigFont: Boolean

        fun getStory(id: Int)
        fun getStoryExtra(id: Int)
        fun isImageUrlExist(url: String): Boolean
        fun addImageUrl(url: String)
        fun addStoryToHistory(bean: ZhihuDetailBean)
        fun isCollectionExist(id: Int)
        fun collectionStory(bean: CollectionStoryBean)
        fun uncollectionStory(id: Int)
        fun loadDownloadImage(url: String)
    }
}
