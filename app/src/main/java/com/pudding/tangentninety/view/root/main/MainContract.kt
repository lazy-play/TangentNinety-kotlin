package com.pudding.tangentninety.view.root.main

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView

/**
 * Created by Error on 2017/8/7 0007.
 */

interface MainContract {
    interface View : BaseView {
        fun setNetState(isWifi: Boolean)
    }

    interface Presenter : BasePresenter<View> {
        val isAutoDownload: Boolean
        fun getNetState()
    }
}
