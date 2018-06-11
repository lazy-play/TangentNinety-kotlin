package com.pudding.tangentninety.view.root.main.section.detail

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

import com.chad.library.adapter.base.BaseQuickAdapter
import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.bean.SectionDetails
import com.pudding.tangentninety.module.bean.SectionStoryBean
import com.pudding.tangentninety.module.event.TopBottomPuddingChange
import com.pudding.tangentninety.view.root.main.storydetail.StoryDetailFragment

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import butterknife.BindView
import com.pudding.tangentninety.utils.loadUrlRound

/**
 * Created by Error on 2017/7/24 0024.
 */

class SectionDetailFragment : BaseFragment<SectionDetailContract.View, SectionDetailPresent>(), SectionDetailContract.View, BaseQuickAdapter.RequestLoadMoreListener {
    @BindView(R.id.background)
    lateinit var backgroundLayout: LinearLayout
    @BindView(R.id.container)
    lateinit var container: RecyclerView
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    private var errorView: View?=null
    lateinit var adapter: SectionDetailAdapter

    private var sectionID: Int = 0
    private var timestamp: Long = 0
    private var isEnterAnimationEnd: Boolean = false
    private var result: SectionDetails?=null

    override fun initInject() {
        fragmentComponent.inject(this)
    }
    override val layout: Int=R.layout.fragment_recyclerview
    override var canSwipeBack=true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        val colorBackground = TypedValue()
        val theme = mContext.theme
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true)
        backgroundLayout.setBackgroundResource(colorBackground.resourceId)
        onPuddingChangeEvent(null)
        var title = arguments?.getString(TITLE_KEY)
        if (title == null) {
            title = ""
        }
        toolbar.title=title
        mActivity.setToolBar(toolbar, true)
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        lastID = sectionID
        lastTime = timestamp
        if (adapter.data.isNotEmpty())
            beans = adapter.data
        super.onDestroyView()
    }

    override fun initEventAndData(savedInstanceState: Bundle?) {
        sectionID = arguments!!.getInt(SECTIONID_KEY, -1)
        adapter = SectionDetailAdapter(this, mPresenter.isNoPic)
        container.layoutManager = LinearLayoutManager(mContext)
        adapter.bindToRecyclerView(container)
        container.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    adapter.resumeLoadImage()
                } else {
                    adapter.pauseLoadImage()
                }
            }
        })
        adapter.setOnLoadMoreListener(this, container)
        adapter.setOnItemChildClickListener{ _, view, position ->
            val r=(view as ImageView).loadUrlRound(adapter.data[position].images!![0],false)
            if (r!!.isComplete) {
                StoryDetailFragment.startStoryDetail(mActivity, adapter.data[position].id)
            }}
        adapter.setOnItemClickListener { _, _, position -> StoryDetailFragment.startStoryDetail(mActivity, adapter.data[position].id) }
        if (lastID == sectionID && beans.isNotEmpty()) {
            adapter.setEmptyView(R.layout.layout_empty_view)
            if (canLoadMore) {
                adapter.setEnableLoadMore(true)
                adapter.loadMoreComplete()
            } else {
                adapter.setEnableLoadMore(false)
                adapter.loadMoreEnd()
            }
            timestamp = lastTime
            adapter.addData(beans)
        } else {
            adapter.disableLoadMoreIfNotFullPage()
            onLoad()

        }
    }

    override fun onEnterAnimationEnd(savedInstanceState: Bundle?) {
        isEnterAnimationEnd = true
        showResult(null)
        super.onEnterAnimationEnd(savedInstanceState)
    }

    private fun onLoad() {
        adapter.setEmptyView(R.layout.layout_loading_view)
        mPresenter.getSectionDetail(sectionID)
    }

    override fun onLoadMoreRequested() {
        mPresenter.getSectionDetailBefore(sectionID, timestamp)
    }

    override fun showResult(resultDetail: SectionDetails?) {
        var result = resultDetail
        if (!isEnterAnimationEnd) {
            this.result = result
            return
        }
        result = if (result == null) this.result else result
        if (result == null)
            return
        adapter.setEmptyView(R.layout.layout_empty_view)
        adapter.setEnableLoadMore(true)
        if (result.stories!!.isEmpty()) {
            adapter.loadMoreEnd()
            canLoadMore = false
            return
        }
        canLoadMore = true
        timestamp = result.timestamp.toLong()
        adapter.addData(result.stories!!)
        adapter.loadMoreComplete()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPuddingChangeEvent(change: TopBottomPuddingChange?) {
            container.setPadding(container.paddingLeft, container.paddingTop, container.paddingRight, mActivity.bottomPadding)
            toolbar.setPadding(toolbar.paddingLeft, mActivity.topPadding, toolbar.paddingRight, 0)
    }


    override fun showErrorMsg(msg: String) {
        if (adapter.data.isNotEmpty()) {
            adapter.loadMoreFail()
            return
        }
        if (errorView == null) {
            errorView = LayoutInflater.from(mContext).inflate(R.layout.layout_error_view, container, false)
            errorView!!.findViewById<View>(R.id.reflash).setOnClickListener { onLoad() }
        }
        adapter.emptyView = errorView
    }

    companion object {
        private const val TITLE_KEY = "Title"
        private const val SECTIONID_KEY = "SectionID"

        private var lastID: Int = 0
        private var lastTime: Long = 0
        private var canLoadMore: Boolean = false
        private lateinit var beans: List<SectionStoryBean>

        fun newInstance(SectionID: Int, title: String): SectionDetailFragment {
            val fragment = SectionDetailFragment()
            val bdl = Bundle(2)
            bdl.putInt(SECTIONID_KEY, SectionID)
            bdl.putString(TITLE_KEY, title)
            fragment.arguments = bdl
            return fragment
        }
    }

}
