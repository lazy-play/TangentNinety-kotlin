package com.pudding.tangentninety.view.root.main.home

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.request.Request
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.pudding.tangentninety.R
import com.pudding.tangentninety.module.bean.HomeHotAdapterBean
import com.pudding.tangentninety.utils.loadUrlRound


/**
 * Created by Error on 2017/7/3 0003.
 */

class HomeAdapter internal constructor(data: List<HomeHotAdapterBean>, fragment: Fragment, private var noPic: Boolean) : BaseQuickAdapter<HomeHotAdapterBean, BaseViewHolder>(data) {
    private var colorTextHotId: Int = 0
    private var colorCardBackgroundId: Int = 0
    private var colorTextTitleId: Int = 0
    private var colorTextTitleClickId: Int = 0

    private var onScroll = false

    init {
        changeTheme(fragment.context!!)
        openLoadAnimation()
        multiTypeDelegate = object : MultiTypeDelegate<HomeHotAdapterBean>() {
            override fun getItemType(bean: HomeHotAdapterBean): Int {
                return bean.type
            }
        }
        multiTypeDelegate
                .registerItemType(HomeHotAdapterBean.TYPE_TITLE, R.layout.item_main_title_view)
                .registerItemType(HomeHotAdapterBean.TYPE_BEAN, R.layout.item_main_view)
    }

    fun setNoPicMode(noPic: Boolean) {
        this.noPic = noPic
    }

    fun changeTheme(context: Context) {
        val theme = context.theme

        val colorTextHot = TypedValue()
        theme.resolveAttribute(R.attr.colorTextHot, colorTextHot, true)
        colorTextHotId = ContextCompat.getColor(context, colorTextHot.resourceId)

        val colorCardBackground = TypedValue()
        theme.resolveAttribute(R.attr.colorCardBackground, colorCardBackground, true)
        colorCardBackgroundId = ContextCompat.getColor(context, colorCardBackground.resourceId)

        val colorTextTitle = TypedValue()
        theme.resolveAttribute(R.attr.colorTextTitle, colorTextTitle, true)
        colorTextTitleId = ContextCompat.getColor(context, colorTextTitle.resourceId)

        val colorTextTitleClick = TypedValue()
        theme.resolveAttribute(R.attr.colorTextTitleClick, colorTextTitleClick, true)
        colorTextTitleClickId = ContextCompat.getColor(context, colorTextTitleClick.resourceId)
    }

    fun setTitleColorClicked(view: View) {
        val text = view.findViewById<TextView>(R.id.title)
        text.setTextColor(colorTextTitleClickId)
    }

    override fun convert(helper: BaseViewHolder, item: HomeHotAdapterBean) {
        when (helper.itemViewType) {
            HomeHotAdapterBean.TYPE_TITLE -> {
                helper.setTextColor(R.id.title, colorTextHotId)
                helper.setText(R.id.title, item.title)
            }
            HomeHotAdapterBean.TYPE_BEAN -> {
                helper.setBackgroundColor(R.id.cardview, colorCardBackgroundId)
                if (item.storyInfoBean!!.isHasRead) {
                    helper.setTextColor(R.id.title, colorTextTitleClickId)
                } else {
                    helper.setTextColor(R.id.title, colorTextTitleId)
                }
                helper.setText(R.id.title, item.storyInfoBean!!.title)
                val image = helper.getView<ImageView>(R.id.image)
                if (noPic) {
                    helper.addOnClickListener(R.id.image)
                }
                val r = image.loadUrlRound(item.storyInfoBean!!.images!![0],onScroll||noPic)
                helper.itemView.setTag(R.id.image_request, r)
                helper.itemView.setTag(R.id.image_url, item.storyInfoBean!!.images!![0])
            }
        }
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
            val r= itemView.getTag(R.id.image_request) as Request?
            if (!noPic &&r!=null&& !r.isComplete) {
                itemView.findViewById<ImageView>(R.id.image).loadUrlRound(itemView.getTag(R.id.image_url) as String,false)
            }
        }
    }
}
