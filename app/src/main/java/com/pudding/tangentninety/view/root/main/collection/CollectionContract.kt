package com.pudding.tangentninety.view.root.main.collection

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.CollectionStoryBean

/**
 * Created by Error on 2017/6/28 0028.
 */

interface CollectionContract {
    interface View : BaseView {
        fun showCollectionList(beans: List<CollectionStoryBean>)
    }

    interface Presenter : BasePresenter<View> {
        fun getCollectionList()
        fun deleteCollections(ids: MutableList<Int>)
    }
}
