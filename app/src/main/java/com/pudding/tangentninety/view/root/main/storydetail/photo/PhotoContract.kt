package com.pudding.tangentninety.view.root.main.storydetail.photo

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView


/**
 * Created by Error on 2017/7/15 0015.
 */

interface PhotoContract {
    interface View : BaseView {
        fun showResult(result: String)
    }

    interface Presenter : BasePresenter<View> {
        fun downloadImage(url: String)
    }
}
