package com.pudding.tangentninety.view.root.main.storydetail.comment

import com.pudding.tangentninety.base.BasePresenter
import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.bean.StoryCommentsBean

/**
 * Created by Error on 2017/7/24 0024.
 */

interface StoryCommentContract {
    interface View : BaseView {
        fun setLongCommentList(longCommentList: StoryCommentsBean)

        fun setShortCommentList(shortCommentList: StoryCommentsBean)
        fun showExtra(num: Int)
    }

    interface Presenter : BasePresenter<View> {
        fun getLongCommentInfo(id: Int)

        fun getLongCommentInfo(id: Int, userid: Int)

        fun getShortCommentInfo(id: Int)

        fun getShortCommentInfo(id: Int, userid: Int)
        fun getStoryExtra(id: Int)
    }
}
