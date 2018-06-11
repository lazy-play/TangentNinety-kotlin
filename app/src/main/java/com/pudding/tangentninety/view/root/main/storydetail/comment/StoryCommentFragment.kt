package com.pudding.tangentninety.view.root.main.storydetail.comment

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

import com.chad.library.adapter.base.BaseQuickAdapter
import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.bean.StoryCommentsBean
import com.pudding.tangentninety.module.event.TopBottomPuddingChange

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.ArrayList

import butterknife.BindView
import com.pudding.tangentninety.view.root.RootActivity
import me.yokeyword.fragmentation.SupportFragment

/**
 * Created by Error on 2017/7/24 0024.
 */

class StoryCommentFragment : BaseFragment<StoryCommentContract.View, StoryCommentPresent>(), StoryCommentContract.View, BaseQuickAdapter.RequestLoadMoreListener {

    private var storyId: Int = 0
    @BindView(R.id.background)
    lateinit var backgroundLayout: LinearLayout
    @BindView(R.id.container)
    lateinit var container: RecyclerView
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    lateinit var adapter: CommentAdapter

    private var errorView: View? = null

    private var shortBeans: MutableList<StoryCommentsBean.CommentsBean>? = null
    private var longBeans: MutableList<StoryCommentsBean.CommentsBean>? = null
    private var totalBean: MutableList<StoryCommentsBean.CommentsBean> = ArrayList()
    private var isLongCommentInit: Boolean = false
    private var isShortCommentInit: Boolean = false
    private var lastId: Int = 0

    override val layout: Int=R.layout.fragment_recyclerview
    override var canSwipeBack=true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        storyId = arguments!!.getInt(STORYID_KEY, -1)
        val colorBackground = TypedValue()
        val theme = mContext.theme
        theme.resolveAttribute(R.attr.colorBackground, colorBackground, true)
        backgroundLayout.setBackgroundResource(colorBackground.resourceId)
        onPuddingChangeEvent(null)
        toolbar.title = "0条评论"
        mActivity.setToolBar(toolbar, true)
    }


    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun setLongCommentList(longCommentList: StoryCommentsBean) {
        isLongCommentInit = true
        longBeans = if (longCommentList.comments!!.isEmpty()) null else longCommentList.comments as MutableList<StoryCommentsBean.CommentsBean>?
        addCommentToTotal()
    }

    override fun setShortCommentList(shortCommentList: StoryCommentsBean) {
        isShortCommentInit = true
        shortBeans = if (shortCommentList.comments!!.isEmpty()) null else shortCommentList.comments as MutableList<StoryCommentsBean.CommentsBean>?
        addCommentToTotal()
    }

    override fun showExtra(num: Int) {
        toolbar.title = "${num}条评论"
    }

    private fun addCommentToTotal() {
        adapter.setEnableLoadMore(true)
        if (!isLongCommentInit || !isShortCommentInit) {
            return
        }
        if (shortBeans == null && longBeans == null) {
            //            if (totalBean.size() != 0)
            loadMoreDate()
            adapter.loadMoreEnd()
            return
        }

        if (longBeans == null && shortBeans != null) {
            totalBean.addAll(shortBeans!!)
            loadMoreDate()
            return
        }
        if (shortBeans == null && longBeans != null) {
            totalBean.addAll(longBeans!!)
            mPresenter.getLongCommentInfo(storyId, longBeans!![longBeans!!.size - 1].id)
            return
        }
        do {
            val shortBean = shortBeans!![0]
            val longBean = longBeans!![0]
            if (shortBean.time >= longBean.time) {
                totalBean.add(shortBean)
                shortBeans!!.removeAt(0)
            } else {
                totalBean.add(longBean)
                longBeans!!.removeAt(0)
            }
        } while (shortBeans!!.size != 0 && longBeans!!.size != 0)
        if (shortBeans!!.size == 0) {
            loadMoreDate()
            return
        } else {
            mPresenter.getLongCommentInfo(storyId, totalBean[totalBean.size - 1].id)
            return
        }
    }

    override fun showErrorMsg(msg: String) {
        if (adapter.data.size != 0) {
            adapter.loadMoreFail()
            return
        }
        if (errorView == null) {
            errorView = LayoutInflater.from(mContext).inflate(R.layout.layout_error_view, container, false)
            errorView!!.findViewById<View>(R.id.reflash).setOnClickListener { load() }
        }
        adapter.emptyView = errorView
    }

    private fun loadMoreDate() {
        adapter.setEmptyView(R.layout.layout_empty_view)
        if (totalBean.size == 0) {
            return
        }
        adapter.addData(totalBean)
        adapter.loadMoreComplete()
        lastId = totalBean[totalBean.size - 1].id
        totalBean.clear()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPuddingChangeEvent(change: TopBottomPuddingChange?) {
            container.setPadding(container.paddingLeft, container.paddingTop, container.paddingRight, mActivity.bottomPadding)
            toolbar.setPadding(toolbar.paddingLeft, mActivity.topPadding, toolbar.paddingRight, 0)
    }

    override fun initInject() {
        fragmentComponent.inject(this)
    }



    override fun initEventAndData(savedInstanceState: Bundle?) {
        totalBean = ArrayList()
        adapter = CommentAdapter(this)
        container.layoutManager = LinearLayoutManager(mContext)
        container.addItemDecoration(DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL))
        adapter.bindToRecyclerView(container)
        adapter.setOnLoadMoreListener(this, container)
        adapter.disableLoadMoreIfNotFullPage()
        load()
    }

    private fun load() {
        adapter.setEmptyView(R.layout.layout_loading_view)
        mPresenter.getShortCommentInfo(storyId)
        mPresenter.getLongCommentInfo(storyId)
        mPresenter.getStoryExtra(storyId)

    }

    override fun onLoadMoreRequested() {
        mPresenter.getShortCommentInfo(storyId, lastId)
    }

    companion object {
        const val STORYID_KEY = "storyID"

        fun startStoryComment(activity: RootActivity, storyID: Int): StoryCommentFragment {
            val fragment = StoryCommentFragment()
            val bdl = Bundle(1)
            bdl.putInt(STORYID_KEY, storyID)
            fragment.arguments = bdl
            activity.start(fragment, SupportFragment.SINGLETOP)
            return fragment
        }
    }
}
