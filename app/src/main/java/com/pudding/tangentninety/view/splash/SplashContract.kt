package com.pudding.tangentninety.view.splash

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.DailyNewListBean

/**
 * This created by Error on 2018/05/30,16:43.
 */
interface SplashContract {
    interface View : BaseView {
        fun loadDailyNewSuccess(bean: DailyNewListBean?)
    }

    interface Presenter : BasePresenter<View> {
        fun loadDailyNewList()
    }
}