package com.pudding.tangentninety.view.root.main.download

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean

/**
 * Created by Error on 2017/7/26 0026.
 */

interface DownloadContract {
    interface View : BaseView {
        fun showDetail(zhihuDetailBean: ZhihuDetailBean)
        fun showDailyNewList(bean: DailyNewListBean)
        fun showDailyBeforeList(bean: DailyListBean)
    }

    interface Presenter : BasePresenter<View> {
        fun loadDailyNewList()
        fun loadDailyBeforeList(before: String)
        fun getStory(id: Int)
        fun addDownloadStory(id: Int)
        fun isStoryDownload(id: Int): Boolean
        fun addImageUrl(url: String)
        fun loadImageUrl(url: String)
    }
}
