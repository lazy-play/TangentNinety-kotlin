package com.pudding.tangentninety.view.root.main

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import com.pudding.tangentninety.BuildConfig
import com.pudding.tangentninety.R
import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.event.AutoDownloadModeChange
import com.pudding.tangentninety.module.event.DownloadNextEvent
import com.pudding.tangentninety.module.event.NightModeEvent
import com.pudding.tangentninety.utils.SystemUtil
import com.pudding.tangentninety.view.root.main.collection.CollectionFragment
import com.pudding.tangentninety.view.root.main.history.HistoryFragment
import com.pudding.tangentninety.view.root.main.home.HomeFragment
import com.pudding.tangentninety.view.root.main.section.SectionFragment
import com.pudding.tangentninety.view.root.main.setting.SettingFragment

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.Timer
import java.util.TimerTask

import butterknife.BindView
import com.pudding.tangentninety.view.root.main.download.DownloadService

/**
 * Created by Error on 2017/6/22 0022.
 */
class MainFragment : BaseFragment<MainContract.View, MainPresent>(), MainContract.View, NavigationView.OnNavigationItemSelectedListener {
    override val layout: Int
        get() = R.layout.fragment_main
    @BindView(R.id.drawer_layout)
    lateinit var drawer: DrawerLayout
    @BindView(R.id.nav_view)
    lateinit var navigationView: NavigationView

    private lateinit var container: ViewGroup
    private var bitmapCover: Bitmap? = null
    private var cover: ImageView? = null
    private lateinit var navHeadView: LinearLayout

    private lateinit var downloadItem: MenuItem

    private lateinit var homeFragment: HomeFragment
    private lateinit var sectionFragment: SectionFragment
    private lateinit var collectionFragment: CollectionFragment
    private lateinit var historyFragment: HistoryFragment
    private lateinit var settingFragment: SettingFragment
    private var clickNum: Int = 0
    private lateinit var headTitle: TextView
    private lateinit var headTitleSmall: TextView
    private var timer: Timer? = null
    private var isExit: Boolean = false

    override fun initEventAndData(savedInstanceState: Bundle?) {
        EventBus.getDefault().register(this)
        val headView = navigationView.getHeaderView(0)
        container = drawer.parent as ViewGroup
        navHeadView = headView.findViewById(R.id.nav_head_view)
        headTitle = navHeadView.findViewById(R.id.nav_head_title)
        headTitleSmall = navHeadView.findViewById(R.id.nav_head_title_small)
        navigationView.setNavigationItemSelectedListener(this)
        navHeadView.setOnClickListener {
            clickNum++
            when {
                clickNum > 30 -> {
                    headTitle.setText(R.string.third_egg)
                    headTitleSmall.setText(R.string.third_egg_info)
                }
                clickNum > 20 -> headTitleSmall.setText(R.string.second_egg_info)
                clickNum > 10 -> {
                    headTitle.setText(R.string.first_egg)
                    headTitleSmall.setText(R.string.first_egg_info)
                }
            }
        }
        if (savedInstanceState != null) {
            homeFragment = findChildFragment(HomeFragment::class.java)
            sectionFragment = findChildFragment(SectionFragment::class.java)
            collectionFragment = findChildFragment(CollectionFragment::class.java)
            historyFragment = findChildFragment(HistoryFragment::class.java)
            settingFragment = findChildFragment(SettingFragment::class.java)
        } else {
            navigationView.setCheckedItem(R.id.nav_home)
            homeFragment = HomeFragment()
            sectionFragment = SectionFragment()
            collectionFragment = CollectionFragment()
            historyFragment = HistoryFragment()
            settingFragment = SettingFragment()
            loadMultipleRootFragment(R.id.main_fragment, 0, homeFragment, sectionFragment, collectionFragment, historyFragment, settingFragment)
            if (mPresenter.isAutoDownload) {
                waitToDownload()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun initInject() {
        fragmentComponent.inject(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DownloadNextEvent) {
        if (event.isFinish) {
            if (event.present == 100) {
                downloadItem.setTitle(R.string.download_success)
                Toast.makeText(mContext, R.string.download_success, Toast.LENGTH_SHORT).show()
            } else {
                downloadItem.setTitle(R.string.download_failed)
                Toast.makeText(mContext, R.string.download_failed, Toast.LENGTH_SHORT).show()
            }
        } else {
            downloadItem.title = Integer.toString(event.present) + "%"
        }
    }

    fun setToolBar(toolbar: Toolbar) {
        mActivity.setToolBar(toolbar, false)
        val toggle = ActionBarDrawerToggle(
                mActivity, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressedSupport(): Boolean {
        when {
            drawer.isDrawerOpen(GravityCompat.START) -> drawer.closeDrawer(GravityCompat.START)
            homeFragment.isHidden -> {
                navigationView.setCheckedItem(R.id.nav_home)
                showHideFragment(homeFragment)
            }
            else -> exitByDoubleClick()
        }
        return true
    }

    private fun exitByDoubleClick() {
        if (!isExit) {
            isExit = true
            Toast.makeText(mContext, R.string.double_exit, Toast.LENGTH_SHORT).show()
            val tExit = Timer()
            tExit.schedule(object : TimerTask() {
                override fun run() {
                    isExit = false//取消退出
                }
            }, 2000)// 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            App.instance.exitApp()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var shouleClose = false
        when (item.itemId) {
            R.id.nav_home -> {
                shouleClose = true
                showHideFragment(homeFragment)
            }
            R.id.nav_section -> {
                shouleClose = true
                showHideFragment(sectionFragment)
            }
            R.id.nav_collection -> {
                shouleClose = true
                showHideFragment(collectionFragment)
            }
            R.id.nav_history -> {
                shouleClose = true
                showHideFragment(historyFragment)
            }
            R.id.nav_setting -> {
                shouleClose = true
                showHideFragment(settingFragment)
            }
            R.id.nav_download -> {
                downloadItem = item
                DownloadService.start(mActivity)
            }
            R.id.nav_theme -> changeTheme()
            R.id.nav_about_me -> AlertDialog.Builder(mContext).setTitle(R.string.action_about_me)
                    .setMessage(String.format(resources.getString(R.string.author_info), BuildConfig.VERSION_NAME))
                    .setPositiveButton(R.string.sure, null).show()
        }
        if (shouleClose) {
            drawer.closeDrawer(GravityCompat.START)
        }
        return true
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(isOpen: AutoDownloadModeChange) {
        if (isOpen.isOpen) {
            waitToDownload()
        } else {
            cancelTimer()
        }
    }

    private fun cancelTimer() {
        if (timer == null) {
            return
        }
        timer!!.cancel()
        timer = null
    }

    private fun waitToDownload() {
        if (timer != null) {
            return
        }

        timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                if (mActivity.isDestroyed) {
                    return
                }
                if (DownloadService.DOWNLOAD_SUCCESS) {
                    return
                }
                if (!SystemUtil.isNetworkAvailable) {
                    return
                }
                mPresenter.getNetState()
            }
        }
        timer!!.schedule(task, 60000)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun changeTheme() {
        if (cover != null) {
            container.removeView(cover)
            cover = null
        }
        if (bitmapCover != null) {
            bitmapCover!!.recycle()
            bitmapCover = null
        }
        cover = ImageView(mContext)
        cover!!.setOnTouchListener { _, _ -> true }
        bitmapCover = SystemUtil.takeScreenShot(mActivity)
        cover!!.setImageBitmap(bitmapCover)
        container.addView(cover)
            val animator = ObjectAnimator.ofFloat(cover!!, "alpha", 0f)
        animator.duration = 400
            animator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (cover != null) {
                        container.removeView(cover)
                        cover = null
                    }
                    if (bitmapCover != null) {
                        bitmapCover!!.recycle()
                        bitmapCover = null
                    }
                }
            })
            animator.start()
        val isNightModeState = !App.appComponent.preferencesHelper().nightModeState
        App.appComponent.preferencesHelper().nightModeState = isNightModeState
        mActivity.setDayNightTheme()
        val theme = mContext.theme

        //切换背景色
        val colorBackground = TypedValue()
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true)
        drawer.setBackgroundResource(colorBackground.resourceId)

        //切换主题颜色
        val mainColor = TypedValue()
        theme.resolveAttribute(R.attr.colorPrimary, mainColor, true)
        navHeadView.setBackgroundResource(mainColor.resourceId)

        //切换侧边栏字体颜色
        val colorNavigation = TypedValue()
        theme.resolveAttribute(R.attr.colorNavigation, colorNavigation, true)
        navigationView.itemIconTintList = AppCompatResources.getColorStateList(mContext, colorNavigation.resourceId)
        navigationView.itemTextColor = AppCompatResources.getColorStateList(mContext, colorNavigation.resourceId)

        //切换侧边栏背景颜色
        val colorUnselectBackground = TypedValue()
        theme.resolveAttribute(R.attr.colorUnselectBackground, colorUnselectBackground, true)
        navigationView.setBackgroundResource(colorUnselectBackground.resourceId)

        //通知其他界面切换颜色
        val nightModeEvent = NightModeEvent()
        nightModeEvent.nightMode = isNightModeState
        EventBus.getDefault().post(nightModeEvent)
    }

    override fun setNetState(isWifi: Boolean) {
        if (isWifi) {
            DownloadService.start(mActivity)
        }
    }
}
