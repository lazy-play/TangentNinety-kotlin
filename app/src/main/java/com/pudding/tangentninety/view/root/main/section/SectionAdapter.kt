package com.pudding.tangentninety.view.root.main.section

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.util.TypedValue
import android.widget.ImageView

import com.bumptech.glide.request.Request
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pudding.tangentninety.R
import com.pudding.tangentninety.module.bean.SectionListBean
import com.pudding.tangentninety.utils.loadUrlRound
import com.pudding.tangentninety.weight.glide.RoundedCornersTransformation

import java.util.ArrayList

/**
 * Created by Error on 2017/7/17 0017.
 */

class SectionAdapter(fragment: Fragment) : BaseQuickAdapter<SectionListBean.DataBean, BaseViewHolder>(R.layout.item_section_view, ArrayList()) {
    private var colorCardBackgroundId: Int = 0
    private var colorTextTitleId: Int = 0
    private var onScroll = false

    init {
        changeTheme(fragment.context!!)
    }

    fun changeTheme(context: Context) {
        val theme = context.theme

        val colorCardBackground = TypedValue()
        theme.resolveAttribute(R.attr.colorCardBackground, colorCardBackground, true)
        colorCardBackgroundId = ContextCompat.getColor(context, colorCardBackground.resourceId)

        val colorTextTitle = TypedValue()
        theme.resolveAttribute(R.attr.colorTextTitle, colorTextTitle, true)
        colorTextTitleId = ContextCompat.getColor(context, colorTextTitle.resourceId)
    }

    override fun convert(helper: BaseViewHolder, item: SectionListBean.DataBean) {
        val cardView = helper.getView<CardView>(R.id.card)
        cardView.setCardBackgroundColor(colorCardBackgroundId)
        helper.setTextColor(R.id.title, colorTextTitleId)
        helper.setText(R.id.title, item.name)
        val image = helper.getView<ImageView>(R.id.image)
        val r = image.loadUrlRound(item.thumbnail!!,onScroll,RoundedCornersTransformation.CornerType.TOP)
        helper.itemView.setTag(R.id.image_request, r)
        helper.itemView.setTag(R.id.image_url, item.thumbnail)
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
            if (r!=null&&!r.isComplete) {
                itemView.findViewById<ImageView>(R.id.image).loadUrlRound(itemView.getTag(R.id.image_url) as String,false,RoundedCornersTransformation.CornerType.TOP)
            }
        }
    }
}
