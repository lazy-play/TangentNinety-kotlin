package com.pudding.tangentninety.view.root.main.home

import android.os.Bundle

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean

/**
 * Created by Error on 2017/7/3 0003.
 */

interface HomeContract {
    interface View : BaseView {
        fun showDailyNewList(bean: DailyNewListBean?, savedInstanceState: Bundle?)
        fun showDailyBeforeList(bean: DailyListBean)
    }

    interface Presenter : BasePresenter<View> {
        val isNoPic: Boolean
        fun loadDailyNewList()
        fun loadDailyBeforeList(before: String)
    }
}
