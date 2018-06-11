package com.pudding.tangentninety.view.root.main.section

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.OrientationHelper
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View

import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.bean.SectionListBean
import com.pudding.tangentninety.module.event.NightModeEvent
import com.pudding.tangentninety.module.event.TopBottomPuddingChange
import com.pudding.tangentninety.view.root.main.MainFragment
import com.pudding.tangentninety.view.root.main.section.detail.SectionDetailFragment

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import butterknife.BindView

/**
 * Created by Error on 2017/6/27 0027.
 */

class SectionFragment : BaseFragment<SectionContract.View, SectionPresent>(), SectionContract.View {
    @BindView(R.id.appbar)
    lateinit var appbar: AppBarLayout
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.container)
    lateinit var container: RecyclerView

    lateinit var adapter: SectionAdapter

    override val layout: Int = R.layout.fragment_recyclerview

    private var errorView: View? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        onPuddingChangeEvent(null)
        val parentFragment = parentFragment
        if (parentFragment is MainFragment) {
            toolbar.title = getString(R.string.action_section)
            parentFragment.setToolBar(toolbar)
        }
    }

    override fun initEventAndData(savedInstanceState: Bundle?) {
        adapter = SectionAdapter(this)
        container.layoutManager = StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL)
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
        adapter.setOnItemClickListener { _, _, position -> mActivity.start(SectionDetailFragment.newInstance(adapter.data[position].id, adapter.data[position].name!!)) }
        onLoad()
    }

    private fun onLoad() {
        adapter.setEmptyView(R.layout.layout_loading_view)
        mPresenter.getSectionList()
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
        adapter.changeTheme(mContext)
        adapter.notifyDataSetChanged()
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

    override fun showSectionList(sectionListBean: SectionListBean) {
        adapter.setEmptyView(R.layout.layout_empty_view)
        adapter.addData(sectionListBean.data!!)
    }
}
