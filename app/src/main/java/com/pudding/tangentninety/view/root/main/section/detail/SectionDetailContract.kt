package com.pudding.tangentninety.view.root.main.section.detail

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.SectionDetails

/**
 * Created by Error on 2017/8/2 0002.
 */

interface SectionDetailContract {
    interface View : BaseView {
        fun showResult(resultDetail: SectionDetails?)
    }

    interface Presenter : BasePresenter<View> {
        val isNoPic: Boolean
        fun getSectionDetail(id: Int)
        fun getSectionDetailBefore(id: Int, timeBefore: Long)
    }
}
