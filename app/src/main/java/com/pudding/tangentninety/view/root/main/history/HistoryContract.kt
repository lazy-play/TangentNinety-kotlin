package com.pudding.tangentninety.view.root.main.history

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.ZhihuDetailBean

/**
 * Created by Error on 2017/6/28 0028.
 */

interface HistoryContract {
    interface View : BaseView {
        fun showHistoryList(beans: List<ZhihuDetailBean>)
    }

    interface Presenter : BasePresenter<View> {
        fun getHistoryList()
    }
}
