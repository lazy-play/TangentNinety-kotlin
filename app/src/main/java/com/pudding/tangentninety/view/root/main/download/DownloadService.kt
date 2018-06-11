package com.pudding.tangentninety.view.root.main.download

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast

import com.pudding.tangentninety.app.Constants
import com.pudding.tangentninety.base.BaseService
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.StoryInfoBean
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.event.DownloadNextEvent
import com.pudding.tangentninety.utils.HtmlUtil
import com.pudding.tangentninety.utils.ImageLoader

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.ArrayList
import java.util.Objects

/**
 * Created by Error on 2017/7/26 0026.
 */

class DownloadService : BaseService<DownloadContract.View, DownloadPresent>(), DownloadContract.View {

    private lateinit var topStorys: MutableList<DailyNewListBean.TopStoriesBean>
    private lateinit var storys: MutableList<StoryInfoBean>
    private lateinit var webView: WebView
    private var lastDay: Int = 0
    private var progress: Int = 0
    private var progressNow: Int = 0

    override fun showDetail(zhihuDetailBean: ZhihuDetailBean) {
        loadImage(zhihuDetailBean.image)
        val htmlData = HtmlUtil.createHtmlData(zhihuDetailBean.body!!)
        webView.loadDataWithBaseURL(null, htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING, null)
    }

    override fun showDailyNewList(bean: DailyNewListBean) {
        if(bean.top_stories!=null)
        topStorys.addAll(bean.top_stories!!)
        if(bean.stories!=null)
        storys.addAll(bean.stories!!)
        lastDay++
        val dateTime = bean.date!!
        mPresenter.loadDailyBeforeList(dateTime)
    }

    private fun loadImage(url: String?) {
        if (url != null)
            mPresenter.loadImageUrl(url)
    }

    override fun showDailyBeforeList(bean: DailyListBean) {
        if (lastDay < Constants.DOWNLOAD_CACHE_TIME) {
            if(bean.stories!=null)
            storys.addAll(bean.stories!!)
            lastDay++
            val dateTime = bean.date!!
                mPresenter.loadDailyBeforeList(dateTime)
        } else {

            progress = topStorys.size + storys.size
            loadStory()
        }
    }

    private fun loadNextStory() {
        if (topStorys.isNotEmpty()) {
            mPresenter.addDownloadStory(topStorys[0].id)
            topStorys.removeAt(0)
        } else if (storys.isNotEmpty()) {
            mPresenter.addDownloadStory(storys[0].id)
            storys.removeAt(0)
        }
        loadStory()
    }

    private fun loadStory() {
        when {
            topStorys.isNotEmpty() -> {
                loadImage(topStorys[0].image)
                mPresenter.getStory(topStorys[0].id)
            }
            storys.isNotEmpty() -> {
                loadImage(storys[0].images!![0])
                mPresenter.getStory(storys[0].id)
            }
            else -> {
                EventBus.getDefault().post(DownloadNextEvent(true))
                DOWNLOAD_SUCCESS = true
                stopSelf()
            }
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initEventAndData() {
        webView = WebView(this)
        topStorys = ArrayList()
        storys = ArrayList()
        val settings = webView.settings
        settings.allowFileAccess = true
        settings.domStorageEnabled = true
        settings.databaseEnabled = true
        settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN
        settings.setSupportZoom(true)
        webView.webViewClient = object : WebViewClient() {
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
                return if (mini != null) {
                    getWebResourceResponse(url, mini)
                } else null
            }

            override fun onPageFinished(view: WebView, url: String) {
                progressNow++
                EventBus.getDefault().post(DownloadNextEvent(progressNow, progress))
                super.onPageFinished(view, url)
            }
        }
        EventBus.getDefault().post(DownloadNextEvent(0, 1))
        EventBus.getDefault().register(this)
        mPresenter.loadDailyNewList()
    }

    private fun getWebResourceResponse(url: String, mime: String?): WebResourceResponse? {
        var response: WebResourceResponse? = null
        try {
            webView.post { mPresenter.addImageUrl(url) }
            response = WebResourceResponse(mime, "UTF-8", FileInputStream(Objects.requireNonNull<File>(ImageLoader.getGlideCache(url))))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        return response
    }

    override fun initInject() {
        serviceComponent.inject(this)
    }

    override fun showErrorMsg(msg: String) {
        EventBus.getDefault().post(DownloadNextEvent(false))
        Toast.makeText(this, "离线下载失败", Toast.LENGTH_SHORT).show()
        stopSelf()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DownloadNextEvent) {
        if (!event.isFinish) {
            loadNextStory()
        }
    }

    companion object {
        var DOWNLOAD_SUCCESS: Boolean = false

        fun start(activity: Activity) {
            activity.startService(Intent(activity, DownloadService::class.java))
        }
    }
}
