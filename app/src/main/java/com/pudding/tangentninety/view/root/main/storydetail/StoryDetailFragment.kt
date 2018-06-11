package com.pudding.tangentninety.view.root.main.storydetail

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.ScaleDrawable
import android.graphics.drawable.TransitionDrawable
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.view.menu.MenuItemImpl
import android.support.v7.widget.ActionMenuView
import android.support.v7.widget.Toolbar
import android.view.*
import android.view.animation.AnticipateOvershootInterpolator
import android.webkit.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView

import com.pudding.tangentninety.R
import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.bean.DetailExtraBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.event.CollectionUpdate
import com.pudding.tangentninety.module.event.HistoryUpdate
import com.pudding.tangentninety.utils.HtmlUtil
import com.pudding.tangentninety.view.root.main.storydetail.comment.StoryCommentFragment
import com.pudding.tangentninety.view.root.main.storydetail.photo.PhotoActivity
import com.pudding.tangentninety.weight.BadgeView

import org.greenrobot.eventbus.EventBus

import java.io.FileInputStream
import java.io.FileNotFoundException

import butterknife.BindView
import com.pudding.tangentninety.module.event.TopBottomPuddingChange
import com.pudding.tangentninety.utils.ImageLoader
import com.pudding.tangentninety.utils.loadUrlWithColorFilter
import com.pudding.tangentninety.view.root.RootActivity
import me.yokeyword.fragmentation.SupportFragment
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Error on 2017/7/6 0006.
 */

class StoryDetailFragment : BaseFragment<StoryDetailContract.View, StoryDetailPresent>(), StoryDetailContract.View {

    var webView: WebView? = null
    var scrollView: NestedScrollView? = null
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.appbar)
    lateinit var appBarLayout: AppBarLayout
    @BindView(R.id.collapsing_toolbar)
    lateinit var collapsingToolbarLayout: CollapsingToolbarLayout
    @BindView(R.id.backdrop)
    lateinit var backdrop: ImageView
    @BindView(R.id.story_title)
    lateinit var title: TextView
    @BindView(R.id.action_menu_view)
    lateinit var menuView: ActionMenuView
    private lateinit var collectionMenu: MenuItem
    private lateinit var goodMenu: BadgeView
    private lateinit var commentsMenu: BadgeView
    private var lastStart: Boolean = false
    private lateinit var collectionBean: CollectionStoryBean
    private lateinit var shareUrl: String


    override val layout: Int = R.layout.fragment_story_detail
    override var canSwipeBack = true
    private var isMenuInit: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        webView = view.findViewById(R.id.webview)
        scrollView = view.findViewById(R.id.scrollview)
        EventBus.getDefault().register(this)
        val id = arguments!!.getInt(STORYID_KEY, -1)
        if (id != StoryID) {
            StoryID = id
        } else {
            lastStart = true
        }
        onCreateOptionsMenu(menuView.menu, mActivity.menuInflater)
        toolbar.title = ""
        mActivity.setToolBar(toolbar, true)
        onPuddingChangeEvent(null)
        init()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPuddingChangeEvent(change: TopBottomPuddingChange?) {
        scrollView?.apply {
            setPadding(paddingLeft, paddingTop, paddingRight, mActivity.bottomPadding)
        }
        (toolbar.layoutParams as FrameLayout.LayoutParams).setMargins(0, mActivity.topPadding, 0, 0)
    }

    private fun setCollectionChecked(checked: Boolean, isEdit: Boolean) {
        collectionMenu.isChecked = checked
        val fill: Drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_collection_fill)!!
        val empty: Drawable = ContextCompat.getDrawable(mContext, R.drawable.ic_collection_empty)!!
        val transitionDrawable: TransitionDrawable = if (checked) TransitionDrawable(arrayOf(empty, fill)) else TransitionDrawable(arrayOf(fill, empty))
        val scaleDrawable = ScaleDrawable(transitionDrawable, Gravity.CENTER, 0.5f, 0.5f)
        transitionDrawable.isCrossFadeEnabled = true
        collectionMenu.icon = scaleDrawable
        transitionDrawable.startTransition(200)
        ObjectAnimator.ofInt(scaleDrawable, "level", 5000, 10000).apply {
            duration = 200
            interpolator = AnticipateOvershootInterpolator()
            start()
        }
        if (isEdit) {
            if (checked) {
                mPresenter.collectionStory(collectionBean)
            } else {
                mPresenter.uncollectionStory(StoryID)
            }
            EventBus.getDefault().post(CollectionUpdate())
        }
    }

    override fun showDetail(zhihuDetailBean: ZhihuDetailBean) {
        mPresenter.addStoryToHistory(zhihuDetailBean)
        EventBus.getDefault().post(HistoryUpdate())
        collectionBean = CollectionStoryBean()
        collectionBean.title = zhihuDetailBean.title
        collectionBean.id = StoryID
        collectionBean.image=zhihuDetailBean.image
        title.text = zhihuDetailBean.title
        shareUrl = zhihuDetailBean.share_url!!
        val noPic = mPresenter.isNoPic
        val isNight = mPresenter.isNightMode
        val islargeFont = mPresenter.isBigFont
        backdrop.loadUrlWithColorFilter(zhihuDetailBean.image!!, noPic)
        val htmlData = HtmlUtil.createHtmlData(zhihuDetailBean.body!!, noPic, isNight, islargeFont)
        webView!!.loadDataWithBaseURL(null, htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING, null)
//   webView!!.loadUrl("file:///android_asset/test.html")
    }

    override fun showExtra(detailExtraBean: DetailExtraBean) {
        commentsMenu.setBadge(detailExtraBean.comments)
        goodMenu.setBadge(detailExtraBean.popularity)
    }

    fun init() {
        scrollToolBar()
        webView!!.settings.apply {
            setAppCacheEnabled(true)
            domStorageEnabled = true
            databaseEnabled = true
            val cacheDirPath = App.instance.cacheDir.absolutePath
            setAppCachePath(cacheDirPath)
            allowFileAccess = true
            cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            javaScriptEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            setSupportZoom(true)
            if (!lastStart)
                blockNetworkImage = true
        }
        val imagePlugin = ImagePlugin()
        webView!!.addJavascriptInterface(imagePlugin, JS_NAME)
        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                startNewWeb(request.url.toString())
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                startNewWeb(url)
                return true
            }

            @Suppress("DEPRECATION")
            @TargetApi(21)
            override fun shouldInterceptRequest(view: WebView, request: WebResourceRequest): WebResourceResponse? {
                return shouldInterceptRequest(view, request.url.toString())
            }

            override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
                var mini: String? = null
                when {
                    url.endsWith("jpg") -> mini = "image/jpg"
                    url.endsWith("png") -> mini = "image/png"
                    url.endsWith("gif") -> mini = "image/gif"
                }
                if (mini != null) {
                    return getWebResourceResponse(url, mini)
                }
                return null
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (lastStart)
                    webView!!.postDelayed({ scrollWebview() }, 50)
                else
                    webView!!.postDelayed({ webView!!.settings.blockNetworkImage = false }, 500)
            }
        }
        mPresenter.getStory(StoryID)
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initEventAndData(savedInstanceState: Bundle?) {

    }

    private fun getWebResourceResponse(url: String, mime: String?): WebResourceResponse? {
        var response: WebResourceResponse? = null
        try {
            response = WebResourceResponse(mime, "UTF-8", FileInputStream(ImageLoader.getGlideCache(url)!!))
            webView?.post { mPresenter.addImageUrl(url) }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return response
    }

    private fun startNewWeb(url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val contentUrl = Uri.parse(url)
        intent.data = contentUrl
        startActivity(intent)
    }

    private fun scrollToolBar() {
        collapsingToolbarLayout.post {
            if (lastStart && toolBarOffset != 0) {
                if (collapsingToolbarLayout.height + toolBarOffset < collapsingToolbarLayout.scrimVisibleHeightTrigger) {
                    collapsingToolbarLayout.setScrimsShown(true, false)
                }
                scrollView!!.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                scrollView!!.dispatchNestedPreScroll(0, -toolBarOffset, null, null)
                scrollView!!.stopNestedScroll()
            }
            appBarLayout.addOnOffsetChangedListener(listener)
        }
    }


    private fun scrollWebview() {
        if (lastStart && webScroll != 0) {
            scrollView?.scrollTo(0, webScroll)
        }
    }


    override fun onDestroyView() {
        webScroll = scrollView!!.scrollY
        appBarLayout.removeOnOffsetChangedListener(listener)
        webView!!.removeJavascriptInterface(JS_NAME)
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isMenuInit)
            return
        isMenuInit = true
        inflater.inflate(R.menu.menu_detail, menu)
        menuView.setOnMenuItemClickListener { item -> onOptionsItemSelected(item) }
        val goodMenuItem = menu.findItem(R.id.good)
        goodMenu = BadgeView(mContext, menuView, goodMenuItem as MenuItemImpl)
        goodMenu.setClickAble(false)
        goodMenuItem.actionView = goodMenu

        val commentsMenuItem = menu.findItem(R.id.comments)
        commentsMenu = BadgeView(mContext, menuView, commentsMenuItem as MenuItemImpl)
        commentsMenuItem.actionView = commentsMenu

        collectionMenu = menu.findItem(R.id.collection)
        mPresenter.isCollectionExist(StoryID)
        mPresenter.getStoryExtra(StoryID)
    }
    override fun setCollectionChecked(checked: Boolean) {
        setCollectionChecked(checked, false)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.share -> {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                val str = "《${title.text}》\n$shareUrl"
                intent.putExtra(Intent.EXTRA_TEXT, str)
                intent.putExtra(Intent.EXTRA_SUBJECT, resources.getText(R.string.app_name))
                intent.type = "text/plain"
                startActivity(Intent.createChooser(intent, "分享到…"))
            }
            R.id.collection -> {
                item.isChecked = !item.isChecked
                setCollectionChecked(item.isChecked, true)
            }
            R.id.comments -> StoryCommentFragment.startStoryComment(mActivity, StoryID)
        }

        return super.onOptionsItemSelected(item)
    }


    override fun loadJsUrl(url: String) {
        webView?.onResume()
        webView?.loadUrl(url)
    }

    override fun initInject() {
        fragmentComponent.inject(this)
    }

    @SuppressLint("JavascriptInterface")
    internal inner class ImagePlugin {
        @JavascriptInterface
        fun loadDownloadImage(url: String) {
            mPresenter.loadDownloadImage(url)
        }

        @JavascriptInterface
        fun openImage(url: String, left: Float, top: Float, right: Float, bottom: Float) {
            val lowUrl = url.toLowerCase()
            val location = IntArray(2)
            webView?.getLocationOnScreen(location)
            PhotoActivity.startPhotoActivity(mActivity, lowUrl, left * App.DIMEN_RATE, top * App.DIMEN_RATE + location[1], right * App.DIMEN_RATE, bottom * App.DIMEN_RATE + location[1], top * App.DIMEN_RATE + location[1] - (collapsingToolbarLayout.height + toolBarOffset))
        }
    }

    companion object {
        private const val STORYID_KEY = "storyID"
        private const val JS_NAME = "ZhihuDaily"
        private var StoryID: Int = 0
        private var webScroll: Int = 0
        private var toolBarOffset: Int = 0

        fun startStoryDetail(activity: RootActivity, storyID: Int) {
            val f = StoryDetailFragment()
            val bdl = Bundle(1)
            bdl.putInt(STORYID_KEY, storyID)
            f.arguments = bdl
            activity.start(f, SupportFragment.SINGLETOP)
        }

        private val listener = AppBarLayout.OnOffsetChangedListener { _, verticalOffset -> toolBarOffset = verticalOffset }
    }
}





