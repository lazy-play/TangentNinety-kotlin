package com.pudding.tangentninety.view.root.main.collection

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import android.widget.Toast

import com.chad.library.adapter.base.BaseQuickAdapter
import com.pudding.tangentninety.R
import com.pudding.tangentninety.base.BaseFragment
import com.pudding.tangentninety.module.bean.CollectionStoryBean
import com.pudding.tangentninety.module.event.CollectionUpdate
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

class CollectionFragment : BaseFragment<CollectionContract.View, CollectionPresent>(), CollectionContract.View, View.OnClickListener {
    @BindView(R.id.appbar)
    lateinit var appbar: AppBarLayout
    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar
    @BindView(R.id.container)
    lateinit var container: RecyclerView

    @BindView(R.id.edit)
    lateinit var edit: TextView
    @BindView(R.id.cancel)
    lateinit var cancel: TextView
    @BindView(R.id.delete)
    lateinit var delete: TextView

    private var isCollectionUpdate: Boolean = false
    private lateinit var adapter: CollectionAdapter


    override val layout: Int=R.layout.fragment_collection

    override fun initEventAndData(savedInstanceState: Bundle?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)
        onPuddingChangeEvent(null)
        val parentFragment=parentFragment
        if (parentFragment is MainFragment) {
            toolbar.setTitle(R.string.action_collection)
            parentFragment.setToolBar(toolbar)
        }
        init()
    }

    override fun onBackPressedSupport(): Boolean {
        if (adapter.isEditMode) {
            showEditMode(false)
            return true
        }
        return false
    }


    private fun init() {
        isCollectionUpdate = true
        adapter = CollectionAdapter(this)
        adapter.setOnItemClickListener { _, view, position ->
            if (adapter.isEditMode) {
                adapter.checkItem(position, view)
            } else {
                StoryDetailFragment.startStoryDetail(mActivity, adapter.data[position].id)
            }
        }
        adapter.onItemLongClickListener = BaseQuickAdapter.OnItemLongClickListener { _, view, position ->
            if (!adapter.isEditMode) {
                showEditMode(true)
                adapter.checkItem(position, view)
            }
            false
        }
        adapter.setOnItemChildClickListener { _, _, position -> adapter.checkItem(position) }
        edit.setOnClickListener(this)
        delete.setOnClickListener(this)
        cancel.setOnClickListener(this)
        container.layoutManager = LinearLayoutManager(mContext)
        adapter.bindToRecyclerView(container)
        adapter.setEmptyView(R.layout.layout_empty_view)
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        update()
    }

    override fun onSupportInvisible() {
        super.onSupportInvisible()
        showEditMode(false)
    }

    private fun update() {
        if (isCollectionUpdate) {
            mPresenter.getCollectionList()
            isCollectionUpdate = false
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
    fun onMessageEvent(update: CollectionUpdate) {
        isCollectionUpdate = true
    }

    override fun showCollectionList(beans: List<CollectionStoryBean>) {
        adapter.setNewData(beans)
    }

    private fun showEditMode(show: Boolean) {
        edit.visibility = if (!show) View.VISIBLE else View.GONE
        delete.visibility = if (show) View.VISIBLE else View.GONE
        cancel.visibility = if (show) View.VISIBLE else View.GONE
        if (show) {
            adapter.openEditMode()
        } else {
            adapter.closeEditMode()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit -> showEditMode(true)
            R.id.cancel -> showEditMode(false)
            R.id.delete -> if (adapter.waitToDelete.size != 0) {
                AlertDialog.Builder(mContext)
                        .setMessage("是否要删除这" + adapter.waitToDelete.size + "个收藏记录")
                        .setPositiveButton(R.string.delete) { dialog, _ ->
                            mPresenter.deleteCollections(adapter.waitToDelete)
                            mPresenter.getCollectionList()
                            showEditMode(false)
                            dialog.dismiss()
                        }
                        .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
                        .show()
            } else {
                Toast.makeText(mActivity, R.string.choose_delete, Toast.LENGTH_SHORT).show()
            }
        }
    }
}
