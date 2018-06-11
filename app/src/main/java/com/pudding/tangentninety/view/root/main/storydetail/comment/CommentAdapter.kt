package com.pudding.tangentninety.view.root.main.storydetail.comment

import android.support.v4.app.Fragment
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.widget.ImageView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pudding.tangentninety.R
import com.pudding.tangentninety.module.bean.StoryCommentsBean
import com.pudding.tangentninety.utils.loadUrlCircle
import com.pudding.tangentninety.weight.ExpandableTextView

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Error on 2017/7/17 0017.
 */

class CommentAdapter(internal var fragment: Fragment) : BaseQuickAdapter<StoryCommentsBean.CommentsBean, BaseViewHolder>(R.layout.item_comment_view, ArrayList()) {
    private var date: Date = Date()
    private var sdf: SimpleDateFormat
    private var year: Int = 0

    init {
        year = date.year

        sdf = SimpleDateFormat("MM-dd HH:mm:ss", Locale.CHINA)
    }

    override fun convert(helper: BaseViewHolder, item: StoryCommentsBean.CommentsBean) {
        date.time = item.time * 1000L
        helper.setText(R.id.user_name, item.author)
                .setText(R.id.good_num, item.likes.toString())
                .setText(R.id.time, if (date.year == year) sdf.format(date) else SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(date))
        val text = helper.getView<ExpandableTextView>(R.id.container)
        text.setText(item.content!!, helper.adapterPosition)
        val icon = helper.getView<ImageView>(R.id.user_icon)
        icon.loadUrlCircle(item.avatar!!,false)
        if (item.reply_to != null && item.reply_to!!.status == 0) {
            val textReply = helper.getView<ExpandableTextView>(R.id.reply_container)
            textReply.visibility = View.VISIBLE
            val str = SpannableString("@" + item.reply_to!!.author + ": " + item.reply_to!!.content)
            str.setSpan(StyleSpan(android.graphics.Typeface.BOLD), 0, item.reply_to!!.author!!.length + 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
            textReply.setText(str, helper.adapterPosition)
        } else {
            helper.setGone(R.id.reply_container, false)
        }
    }
}
