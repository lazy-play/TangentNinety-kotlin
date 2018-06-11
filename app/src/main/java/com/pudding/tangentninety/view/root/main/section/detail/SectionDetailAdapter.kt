package com.pudding.tangentninety.view.root.main.section.detail

import android.support.v4.app.Fragment
import android.widget.ImageView

import com.bumptech.glide.request.Request
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pudding.tangentninety.R
import com.pudding.tangentninety.module.bean.SectionStoryBean
import com.pudding.tangentninety.utils.loadUrlRound

import java.util.ArrayList

/**
 * Created by Error on 2017/7/17 0017.
 */

class SectionDetailAdapter(private val fragment: Fragment, private val isNoPic: Boolean) : BaseQuickAdapter<SectionStoryBean, BaseViewHolder>(R.layout.item_main_view, ArrayList()) {
    private var onScroll = false
    override fun convert(helper: BaseViewHolder, item: SectionStoryBean) {
        helper.setText(R.id.title, item.title)
        val image:ImageView = helper.getView(R.id.image)
        if (isNoPic) {
            helper.addOnClickListener(R.id.image)
        }
        val r = image.loadUrlRound(item.images!![0],onScroll||isNoPic)
        helper.itemView.setTag(R.id.image_request, r)
        helper.itemView.setTag(R.id.image_url, item.images!![0])
    }

    fun pauseLoadImage() {
        onScroll = true
    }

    fun resumeLoadImage() {
        onScroll = false
        val recycler = recyclerView
        val count = recycler.childCount
        for (i in 0 until count) {
            val itemView = recycler.getChildAt(i)
            val r = itemView.getTag(R.id.image_request) as Request?
            if (!isNoPic &&r!=null&&!r.isComplete) {
                itemView.findViewById<ImageView>(R.id.image).loadUrlRound(itemView.getTag(R.id.image_url) as String, false)
            }
        }
    }
}
