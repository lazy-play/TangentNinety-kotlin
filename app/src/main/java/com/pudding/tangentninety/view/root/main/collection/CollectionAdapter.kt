package com.pudding.tangentninety.view.root.main.collection

import android.content.Context
import android.content.res.ColorStateList
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.util.TypedValue
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pudding.tangentninety.R
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.utils.loadUrlRound

import java.util.ArrayList

/**
 * Created by Error on 2017/7/17 0017.
 */

class CollectionAdapter internal constructor(fragment: Fragment) : BaseQuickAdapter<CollectionStoryBean, BaseViewHolder>(R.layout.item_main_view, ArrayList()) {
    private var colorTextTitleId: Int = 0
    private var colorCardBackgroundId: Int = 0
    private lateinit var checkBoxColorId: ColorStateList
    var isEditMode: Boolean = false
        private set

    var waitToDelete: MutableList<Int>

    init {
        changeTheme(fragment.context!!)
        waitToDelete = ArrayList()
    }

    fun changeTheme(context: Context) {
        val theme = context.theme
        val colorTextTitle = TypedValue()
        theme.resolveAttribute(R.attr.colorTextTitle, colorTextTitle, true)
        colorTextTitleId = ContextCompat.getColor(context, colorTextTitle.resourceId)

        val colorCardBackground = TypedValue()
        theme.resolveAttribute(R.attr.colorCardBackground, colorCardBackground, true)
        colorCardBackgroundId = ContextCompat.getColor(context, colorCardBackground.resourceId)

        val checkBoxColor = TypedValue()
        theme.resolveAttribute(R.attr.colorNavigation, checkBoxColor, true)
        checkBoxColorId = AppCompatResources.getColorStateList(context, checkBoxColor.resourceId)
    }

    override fun convert(helper: BaseViewHolder, item: CollectionStoryBean) {
        if (isEditMode) {
            helper.setGone(R.id.checkBox, true)
                    .setChecked(R.id.checkBox, item.isChecked)
                    .addOnClickListener(R.id.checkBox)
        } else {
            helper.setGone(R.id.checkBox, false)
        }
        helper.setBackgroundColor(R.id.cardview, colorCardBackgroundId)
        helper.setTextColor(R.id.title, colorTextTitleId)
                .setText(R.id.title, item.title)
        helper.getView<CheckBox>(R.id.checkBox).buttonTintList = checkBoxColorId
        item.image?.let { helper.getView<ImageView>(R.id.image).loadUrlRound(it,false) }
    }

    fun openEditMode() {
        isEditMode = true
        notifyDataSetChanged()
    }

    fun checkItem(position: Int, view: View? = null) {
        val check = !data[position].isChecked
        if (check) {
            waitToDelete.add(data[position].id)
        } else {
            waitToDelete.remove(Integer.valueOf(data[position].id))
        }
        data[position].isChecked = check
        if (view != null) {
            (view.findViewById<CheckBox>(R.id.checkBox)).isChecked = check
        }
    }

//    fun getWaitToDelete(): List<Int>? {
//        return waitToDelete
//    }

    fun closeEditMode() {
        isEditMode = false
        data.forEach {
            it.isChecked = false
        }
        waitToDelete.clear()
        notifyDataSetChanged()
    }
}
