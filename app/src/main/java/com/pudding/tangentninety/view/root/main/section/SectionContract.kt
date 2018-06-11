package com.pudding.tangentninety.view.root.main.section

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.SectionListBean

/**
 * Created by Error on 2017/7/31 0031.
 */

interface SectionContract {
    interface View : BaseView {
        fun showSectionList(sectionListBean: SectionListBean)
    }

    interface Presenter : BasePresenter<View> {
        fun getSectionList()
    }
}
