package com.pudding.tangentninety.view.root.main.home

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.widget.AppBarLayout
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.bean.DailyListBean
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.module.bean.HomeHotAdapterBean
import com.pudding.tangentninety.module.event.NightModeEvent
import com.pudding.tangentninety.module.event.NoPicEvent
import com.pudding.tangentninety.module.event.TopBottomPuddingChange
import com.pudding.tangentninety.utils.SystemUtil
import com.pudding.tangentninety.view.root.main.MainFragment
import com.pudding.tangentninety.view.root.main.storydetail.StoryDetailFragment
import com.youth.banner.Banner

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import java.util.ArrayList
import java.util.Objects

import butterknife.BindView
import com.pudding.tangentninety.utils.loadUrl
import com.pudding.tangentninety.utils.loadUrlRound


/**
 * Created by Error on 2017/6/26 0026.
 */

class HomeFragment : BaseFragment<HomeContract.View, HomePresent>(), HomeContract.View, SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.appbar)
    lateinit var appbar: AppBarLayout
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.container)
    lateinit var container: RecyclerView
    @BindView(R.id.reflash)
    lateinit var reflash: SwipeRefreshLayout

    private var isNoPic: Boolean = false
    private lateinit var banner: Banner
    private lateinit var bannerTitle: TextView
    private var errorView: View?=null
    private lateinit var layoutManager: LinearLayoutManager

    private var savedInstanceState: Bundle? = null
    private lateinit var topStories: MutableList<DailyNewListBean.TopStoriesBean>
    private lateinit var adapter: HomeAdapter
    private lateinit var lastDay: String

    override val layout: Int=R.layout.fragment_reflash_recyclerview


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        this.savedInstanceState = savedInstanceState
        onPuddingChangeEvent(null)
        val parentFragment =parentFragment
        if (parentFragment is MainFragment) {
            toolbar.setTitle(R.string.action_home)
            parentFragment.setToolBar(toolbar)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (savedInstanceState != null) {
            outState.putParcelableArrayList(TYPE_TOP_STOTRYS, savedInstanceState!!.getParcelableArrayList(TYPE_TOP_STOTRYS))
            outState.putParcelableArrayList(TYPE_STOTRYS, savedInstanceState!!.getParcelableArrayList(TYPE_STOTRYS))
            outState.putInt(TYPE_OFFSET, savedInstanceState!!.getInt(TYPE_OFFSET))
            outState.putInt(TYPE_POSITION, savedInstanceState!!.getInt(TYPE_POSITION))
            outState.putString(TYPE_DATE, savedInstanceState!!.getString(TYPE_DATE))
            return
        }
        if (adapter.data.isEmpty()) {
            return
        }
        val position = layoutManager.findFirstVisibleItemPosition()
        val firstVisiableChildView = layoutManager.findViewByPosition(position)!!
        outState.putParcelableArrayList(TYPE_TOP_STOTRYS, topStories as ArrayList<DailyNewListBean.TopStoriesBean>)
        outState.putParcelableArrayList(TYPE_STOTRYS, adapter.data as ArrayList<HomeHotAdapterBean>)
        outState.putInt(TYPE_OFFSET, firstVisiableChildView.top)
        outState.putInt(TYPE_POSITION, position)
        outState.putString(TYPE_DATE, lastDay)
    }

    override fun initEventAndData(savedInstanceState: Bundle?) {
        topStories = ArrayList()
        layoutManager = LinearLayoutManager(mContext)
        container.layoutManager = layoutManager
        container.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    adapter.resumeLoadImage()
                } else {
                    adapter.pauseLoadImage()
                }
            }
        })
        isNoPic = mPresenter.isNoPic
        adapter = HomeAdapter(ArrayList(), this, isNoPic)
        adapter.bindToRecyclerView(container)
        adapter.setOnItemChildClickListener { _, view, position ->
            val r=(view as ImageView).loadUrlRound(adapter.data[position].storyInfoBean!!.images!![0],false)
            if (r!!.isComplete) {
                StoryDetailFragment.startStoryDetail(mActivity, adapter.data[position].storyInfoBean!!.id)
            }
        }
        adapter.setOnItemClickListener { baseQuickAdapter, view, position ->
            if (adapter.getItemViewType(position + baseQuickAdapter.headerLayoutCount) == HomeHotAdapterBean.TYPE_BEAN) {
                adapter.data[position].storyInfoBean!!.isHasRead = true
                adapter.setTitleColorClicked(view)
                StoryDetailFragment.startStoryDetail(mActivity, adapter.data[position].storyInfoBean!!.id)
            }
        }
        adapter.setOnLoadMoreListener({ mPresenter.loadDailyBeforeList(lastDay!!) }, container)
        adapter.disableLoadMoreIfNotFullPage()
        reflash.setOnRefreshListener(this)
        banner = LayoutInflater.from(mContext).inflate(R.layout.item_banner_view, container, false) as Banner
        bannerTitle = banner.findViewById(R.id.story_title)
        banner.setImageLoader(object : com.youth.banner.loader.ImageLoader() {
            override fun displayImage(context: Context, path: Any, imageView: ImageView) {
                imageView.loadUrl((path as DailyNewListBean.TopStoriesBean).image!!,isNoPic)
            }
        })
        banner.setOnBannerListener { position -> StoryDetailFragment.startStoryDetail(mActivity, topStories[position].id) }
        banner.setOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                bannerTitle.text = topStories[position].title
            }
        })
        when {
            savedInstanceState?.getParcelableArrayList<Parcelable>(TYPE_STOTRYS) != null -> {
                showDailyNewList(null, savedInstanceState)
                this.savedInstanceState = null
            }
            mActivity.bean!=null -> {
                showDailyNewList(mActivity.bean,null)
                mActivity.bean=null
            }
            else -> onRefresh()
        }
    }

    override fun showDailyNewList(bean: DailyNewListBean?, savedInstanceState: Bundle?) {
        reflash.isRefreshing = false
        adapter.setEmptyView(R.layout.layout_empty_view)
        val lists: MutableList<HomeHotAdapterBean>?
        val topLists: List<DailyNewListBean.TopStoriesBean>?
        if (savedInstanceState != null) {
            lastDay = savedInstanceState.getString(TYPE_DATE)
            lists = savedInstanceState.getParcelableArrayList(TYPE_STOTRYS)
            topLists = savedInstanceState.getParcelableArrayList(TYPE_TOP_STOTRYS)

        } else {
            if (bean == null || bean.stories==null||bean.stories!!.isEmpty()) {
                return
            }
            if (adapter.data.size != 0) {
                if (adapter.data[1].storyInfoBean == bean.stories!![0]) {
                    Toast.makeText(mActivity, R.string.already_new, Toast.LENGTH_SHORT).show()
                    return
                }
            }
            lastDay = bean.date!!
            lists = ArrayList()
            lists.add(HomeHotAdapterBean(resources.getString(R.string.daily_hot)))
            bean.stories?.forEach{
                lists.add(HomeHotAdapterBean(it))
            }
            topLists = bean.top_stories
        }
        adapter.data.clear()
        topStories.clear()
        topStories.addAll(topLists!!)
        banner.setImages(topStories)
        adapter.addData(Objects.requireNonNull<List<HomeHotAdapterBean>>(lists))
        adapter.setEnableLoadMore(true)
        if (adapter.headerLayoutCount == 0) {
            adapter.addHeaderView(banner)
        }
        if (savedInstanceState != null)
            layoutManager.scrollToPositionWithOffset(savedInstanceState.getInt(TYPE_POSITION), savedInstanceState.getInt(TYPE_OFFSET))
        banner.start()
        banner.startAutoPlay()
    }

    override fun showDailyBeforeList(bean: DailyListBean) {
        lastDay = bean.date!!
        val lists = ArrayList<HomeHotAdapterBean>()
        lists.add(HomeHotAdapterBean(SystemUtil.getDateFormat(lastDay!!)!!))
        bean.stories?.forEach{
            lists.add(HomeHotAdapterBean(it))
        }
        adapter.addData(lists)
        adapter.loadMoreComplete()
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun showErrorMsg(msg: String) {
        if (adapter.data.size != 0) {
            adapter.loadMoreFail()
            return
        }
        if (errorView == null) {
            errorView = LayoutInflater.from(mContext).inflate(R.layout.layout_error_view, container, false)
            errorView!!.findViewById<View>(R.id.reflash).setOnClickListener { onRefresh() }
        }
        adapter.emptyView = errorView
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
    fun onMessageEvent(event: NoPicEvent) {
        isNoPic = mPresenter.isNoPic
        adapter.setNoPicMode(isNoPic)
    }

    override fun initInject() {
        fragmentComponent.inject(this)
    }


    override fun onRefresh() {
        adapter.setEmptyView(R.layout.layout_loading_view)
        mPresenter.loadDailyNewList()
    }

    companion object {
        private const val TYPE_TOP_STOTRYS = "TOP_STOTRYS"
        private const val TYPE_STOTRYS = "STOTRYS"
        private const val TYPE_POSITION = "POSITION"
        private const val TYPE_OFFSET = "OFFSET"
        private const val TYPE_DATE = "DATE"
    }

}
