package com.pudding.tangentninety.view.root.main.setting

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.ContextCompat
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast

import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.event.AutoDownloadModeChange
import com.pudding.tangentninety.module.event.NightModeEvent
import com.pudding.tangentninety.module.event.TopBottomPuddingChange
import com.pudding.tangentninety.view.root.main.MainFragment

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import butterknife.BindView

/**
 * Created by Error on 2017/6/27 0027.
 */

class SettingFragment : BaseFragment<SettingContract.View, SettingPresent>(), SettingContract.View, View.OnClickListener {
    @BindView(R.id.appbar)
    lateinit var appbar: AppBarLayout
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.title_normal)
    lateinit var titleNormal: TextView
    @BindView(R.id.title_other)
    lateinit var titleOther: TextView

    @BindView(R.id.clear_cache_title)
    lateinit var clearCacheTitle: TextView
    @BindView(R.id.download_title)
    lateinit var downloadTitle: TextView
    @BindView(R.id.nopic_title)
    lateinit var nopicTitle: TextView
    @BindView(R.id.big_font_title)
    lateinit var bigFontTitle: TextView

    @BindView(R.id.download_subtitle)
    lateinit var downloadSubtitle: TextView
    @BindView(R.id.nopic_subtitle)
    lateinit var nopicSubtitle: TextView
    @BindView(R.id.clear_cache_subtitle)
    lateinit var clearCacheSubtitle: TextView

    @BindView(R.id.download_check)
    lateinit var downloadCheck: CheckBox
    @BindView(R.id.nopic_check)
    lateinit var nopicCheck: CheckBox
    @BindView(R.id.big_font_check)
    lateinit var bigFontCheck: CheckBox

    @BindView(R.id.download)
    lateinit var download: View
    @BindView(R.id.nopic)
    lateinit var nopic: View
    @BindView(R.id.big_font)
    lateinit var bigFont: View
    @BindView(R.id.clear_cache)
    lateinit var clearCache: View

    override val layout: Int=R.layout.fragment_setting

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        onPuddingChangeEvent(null)
        val parentFragment= parentFragment
        if (parentFragment is MainFragment) {
            toolbar.title=getString(R.string.action_setting)
            parentFragment.setToolBar(toolbar)
        }
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        clearCacheSubtitle.text=mPresenter.cacheSize
    }

    override fun initEventAndData(savedInstanceState: Bundle?) {
        nopic.setOnClickListener(this)
        bigFont.setOnClickListener(this)
        download.setOnClickListener(this)
        clearCache.setOnClickListener(this)
        nopicCheck.setOnClickListener(this)
        bigFontCheck.setOnClickListener(this)
        downloadCheck.setOnClickListener(this)
        nopicCheck.isChecked = mPresenter.isNoPic
        bigFontCheck.isChecked = mPresenter.isBigFont
        downloadCheck.isChecked = mPresenter.isAutoCache
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun initInject() {
        fragmentComponent.inject(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NightModeEvent) {
        val theme = mContext.theme
        val mainColor = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, mainColor, true)
        appbar.setBackgroundResource(mainColor.resourceId)

        val menuTitleColor = TypedValue()
        theme.resolveAttribute(R.attr.colorMenuTitle, menuTitleColor, true)
        titleNormal.setTextColor(ContextCompat.getColor(mContext, menuTitleColor.resourceId))
        titleOther.setTextColor(ContextCompat.getColor(mContext, menuTitleColor.resourceId))

        val titleColor = TypedValue()
        theme.resolveAttribute(R.attr.colorTextTitle, titleColor, true)
        clearCacheTitle.setTextColor(ContextCompat.getColor(mContext, titleColor.resourceId))
        downloadTitle.setTextColor(ContextCompat.getColor(mContext, titleColor.resourceId))
        nopicTitle.setTextColor(ContextCompat.getColor(mContext, titleColor.resourceId))
        bigFontTitle.setTextColor(ContextCompat.getColor(mContext, titleColor.resourceId))

        val subTitleColor = TypedValue()
        theme.resolveAttribute(R.attr.colorTextTitleClick, subTitleColor, true)
        downloadSubtitle.setTextColor(ContextCompat.getColor(mContext, subTitleColor.resourceId))
        nopicSubtitle.setTextColor(ContextCompat.getColor(mContext, subTitleColor.resourceId))
        clearCacheSubtitle.setTextColor(ContextCompat.getColor(mContext, subTitleColor.resourceId))

        val checkBoxColor = TypedValue()
        theme.resolveAttribute(R.attr.colorNavigation, checkBoxColor, true)
        downloadCheck.buttonTintList = AppCompatResources.getColorStateList(mContext, checkBoxColor.resourceId)
        bigFontCheck.buttonTintList = AppCompatResources.getColorStateList(mContext, checkBoxColor.resourceId)
        nopicCheck.buttonTintList = AppCompatResources.getColorStateList(mContext, checkBoxColor.resourceId)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPuddingChangeEvent(change: TopBottomPuddingChange?) {
            toolbar.setPadding(0, mActivity.topPadding, 0, 0)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.download -> {
                val downloadState = !downloadCheck.isChecked
                downloadCheck.isChecked = downloadState
                mPresenter.setAutoCacheState(downloadCheck.isChecked)
                EventBus.getDefault().post(AutoDownloadModeChange(downloadCheck.isChecked))
            }
            R.id.download_check -> {
                mPresenter.setAutoCacheState(downloadCheck.isChecked)
                EventBus.getDefault().post(AutoDownloadModeChange(downloadCheck.isChecked))
            }
            R.id.nopic -> {
                val nopicState = !nopicCheck.isChecked
                nopicCheck.isChecked = nopicState
                mPresenter.setNoPicState(nopicCheck.isChecked)
            }
            R.id.nopic_check -> mPresenter.setNoPicState(nopicCheck.isChecked)
            R.id.big_font -> {
                val bigState = !bigFontCheck.isChecked
                bigFontCheck.isChecked = bigState
                mPresenter.setBigFontState(bigFontCheck.isChecked)
            }
            R.id.big_font_check -> mPresenter.setBigFontState(bigFontCheck.isChecked)
            R.id.clear_cache -> {
                mPresenter.clearCache()
                v.postDelayed({
                    clearCacheSubtitle.text=mPresenter.cacheSize
                    Toast.makeText(mActivity, "清除成功", Toast.LENGTH_SHORT).show()
                }, 500)
            }
        }
    }
}
