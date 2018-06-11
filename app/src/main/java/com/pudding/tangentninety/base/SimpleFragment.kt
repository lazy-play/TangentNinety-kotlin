package com.pudding.tangentninety.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.pudding.tangentninety.weight.swipeback.SwipeBackFragment
import com.pudding.tangentninety.weight.swipeback.SwipeBackLayout

import butterknife.ButterKnife
import butterknife.Unbinder
import com.pudding.tangentninety.view.root.RootActivity

/**
 * Created by Error on 2017/6/22 0022.
 */

abstract class SimpleFragment : SwipeBackFragment() {

    protected lateinit var mView: View
    protected lateinit var mActivity: RootActivity
    protected lateinit var mContext: Context
    private lateinit var mUnBinder: Unbinder


    protected abstract val layout: Int

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as RootActivity
        mContext = context

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (canSwipeBack) {
            val v = inflater.inflate(layout, null)
            mView = attachToSwipeBack(v)
            setSwipeBackEnable(canSwipeBack)
            swipeBackLayout.setEdgeLevel(SwipeBackLayout.EdgeLevel.MAX)
        } else {
            mView = inflater.inflate(layout, null)
        }
        return mView
    }
    protected open var canSwipeBack: Boolean=false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mUnBinder = ButterKnife.bind(this, view)
    }


    override fun onLazyInitView(savedInstanceState: Bundle?) {
        super.onLazyInitView(savedInstanceState)
        initEventAndData(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mUnBinder.unbind()
        mActivity.setToolBar(null, false)
    }

    protected abstract fun initEventAndData(savedInstanceState: Bundle?)
}
