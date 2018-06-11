package com.pudding.tangentninety.view.root.main.setting

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView

/**
 * Created by Error on 2017/6/28 0028.
 */

interface SettingContract {
    interface View : BaseView
    interface Presenter : BasePresenter<View> {
        val isNoPic: Boolean
        val isBigFont: Boolean
        val isAutoCache: Boolean
        val cacheSize: String
        fun setNoPicState(state: Boolean)
        fun setBigFontState(state: Boolean)
        fun setAutoCacheState(state: Boolean)
        fun clearCache()
    }
}
