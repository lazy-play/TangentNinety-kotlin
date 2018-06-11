package com.pudding.tangentninety.weight.swipeback

import android.graphics.Color
import android.os.Bundle
import android.support.annotation.FloatRange
import android.support.v4.app.Fragment
import android.view.View
import android.view.ViewGroup

import me.yokeyword.fragmentation.ISupportFragment

/**
 * Created by YoKey on 17/6/29.
 */

class SwipeBackFragmentDelegate(swipeBackFragment: ISwipeBackFragment) {
    private val mFragment: Fragment
    private val mSupport: ISupportFragment
    lateinit var swipeBackLayout: SwipeBackLayout
        private set

    init {
        if (swipeBackFragment !is Fragment || swipeBackFragment !is ISupportFragment)
            throw RuntimeException("Must extends Fragment and implements ISupportFragment!")
        mFragment = swipeBackFragment
        mSupport = swipeBackFragment
    }

    fun onCreate(savedInstanceState: Bundle?) {
        onFragmentCreate()
    }

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (view is SwipeBackLayout) {
            val childView = view.getChildAt(0)
            mSupport.supportDelegate.setBackground(childView)
        } else {
            mSupport.supportDelegate.setBackground(view)
        }
    }

    fun attachToSwipeBack(view: View): View {
        swipeBackLayout.attachToFragment(mSupport, view)
        return swipeBackLayout
    }

    fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel) {
        swipeBackLayout.setEdgeLevel(edgeLevel)
    }

    fun setEdgeLevel(widthPixel: Int) {
        swipeBackLayout.setEdgeLevel(widthPixel)
    }

    fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            swipeBackLayout.hiddenFragment()
        }
    }

    fun setSwipeBackEnable(enable: Boolean) {
        swipeBackLayout.setEnableGesture(enable)
    }

    /**
     * Set the offset of the parallax slip.
     */
    fun setParallaxOffset(@FloatRange(from = 0.0, to = 1.0) offset: Float) {
        swipeBackLayout.setParallaxOffset(offset)
    }

    fun onDestroyView() {
        swipeBackLayout.internalCallOnDestroyView()
    }

    private fun onFragmentCreate() {
        if (mFragment.context == null) return

        swipeBackLayout = SwipeBackLayout(mFragment.context!!)
        val params = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        swipeBackLayout.layoutParams = params
        swipeBackLayout.setBackgroundColor(Color.TRANSPARENT)
    }
}
