package com.pudding.tangentninety.weight

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.view.menu.ActionMenuItemView
import android.support.v7.view.menu.MenuItemImpl
import android.support.v7.widget.ActionMenuView
import android.view.LayoutInflater
import android.widget.RelativeLayout
import android.widget.TextView

import com.pudding.tangentninety.R

/**
 * This created by Error on 2018/03/26,15:31.
 */
class BadgeView(context: Context, menuView: ActionMenuView, itemData: MenuItemImpl) : RelativeLayout(context) {
    private lateinit var mIvIcon: ActionMenuItemView
    private lateinit var mTvBadge: TextView

    init {
        init(menuView, itemData)
    }

    @SuppressLint("RestrictedApi")
    private fun init(menuView: ActionMenuView, itemData: MenuItemImpl) {
        LayoutInflater.from(context).inflate(R.layout.badge_menu, this, true)
        mIvIcon = findViewById(R.id.iv_icon)
        mTvBadge = findViewById(R.id.tv_badge)
        mIvIcon.initialize(itemData, 0)
        mIvIcon.setItemInvoker(menuView)
    }

    fun setClickAble(clickAble: Boolean) {
        mIvIcon.isClickable = clickAble
    }

    /**
     * 设置显示的数字。
     *
     * @param i 数字。
     */
    fun setBadge(i: Int) {
        if (i > 999) {
            mTvBadge.text = resources.getText(R.string.more_than_1000)
        } else {
            mTvBadge.text = i.toString()
        }
    }

}
