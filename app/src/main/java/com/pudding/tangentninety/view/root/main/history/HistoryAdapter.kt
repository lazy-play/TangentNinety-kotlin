package com.pudding.tangentninety.view.root.main.history

import android.content.Context
import android.support.v4.content.ContextCompat
import android.util.TypedValue

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pudding.tangentninety.R
import com.pudding.tangentninety.module.bean.ZhihuDetailBean

import java.util.ArrayList

/**
 * Created by Error on 2017/7/17 0017.
 */

class HistoryAdapter internal constructor(context: Context) : BaseQuickAdapter<ZhihuDetailBean, BaseViewHolder>(R.layout.item_history_view, ArrayList()) {
    private var colorTextTitleId: Int = 0

    init {
        changeTheme(context)
    }

    fun changeTheme(context: Context) {
        val theme = context.theme
        val colorTextTitleClick = TypedValue()
        theme.resolveAttribute(R.attr.colorTextTitleClick, colorTextTitleClick, true)
        colorTextTitleId = ContextCompat.getColor(context, colorTextTitleClick.resourceId)
    }

    override fun convert(helper: BaseViewHolder, item: ZhihuDetailBean) {
        helper.setTextColor(R.id.title, colorTextTitleId)
        helper.setText(R.id.title, item.title)
    }
}
