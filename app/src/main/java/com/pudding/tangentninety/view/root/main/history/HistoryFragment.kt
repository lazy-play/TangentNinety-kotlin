package com.pudding.tangentninety.view.root.main.history

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.View

import com.chad.library.adapter.base.BaseQuickAdapter
import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.bean.ZhihuDetailBean
import com.pudding.tangentninety.module.event.HistoryUpdate
import com.pudding.tangentninety.module.event.NightModeEvent
import com.pudding.tangentninety.module.event.TopBottomPuddingChange
import com.pudding.tangentninety.view.root.main.MainFragment
import com.pudding.tangentninety.view.root.main.storydetail.StoryDetailFragment

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import butterknife.BindView

/**
 * Created by Error on 2017/6/27 0027.
 */

class HistoryFragment : BaseFragment<HistoryContract.View, HistoryPresent>(), HistoryContract.View {
    @BindView(R.id.appbar)
    lateinit var appbar: AppBarLayout
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.container)
    lateinit var container: RecyclerView

    private var isHistoryUpdate: Boolean = false
    private lateinit var adapter: HistoryAdapter

    override val layout: Int=R.layout.fragment_recyclerview


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        onPuddingChangeEvent(null)
        val parentFragment= parentFragment
        if (parentFragment is MainFragment) {
            toolbar.setTitle(R.string.action_history)
            parentFragment.setToolBar(toolbar)
        }
        init()
    }

    override fun initEventAndData(savedInstanceState: Bundle?) {}

    override fun onSupportVisible() {
        super.onSupportVisible()
        if (isHistoryUpdate) {
            mPresenter.getHistoryList()
            isHistoryUpdate = false
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(update: HistoryUpdate) {
        isHistoryUpdate = true
    }

    private fun init() {
        isHistoryUpdate = true
        adapter = HistoryAdapter(mContext)
        adapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { _, _, position -> StoryDetailFragment.startStoryDetail(mActivity, adapter.data[position].id) }
        container.layoutManager = LinearLayoutManager(mContext)
        container.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        adapter.bindToRecyclerView(container)
        adapter.setEmptyView(R.layout.layout_empty_view)
    }

    override fun showHistoryList(beans: List<ZhihuDetailBean>) {
        adapter.setNewData(beans)
    }
}
