package com.pudding.tangentninety.weight.swipeback

import android.os.Bundle
import android.support.annotation.FloatRange
import android.view.View

import me.yokeyword.fragmentation.SupportFragment

/**
 * You can also refer to [SwipeBackFragment] to implement YourSwipeBackFragment
 * (extends Fragment and impl [me.yokeyword.fragmentation.ISupportFragment])
 *
 *
 * Created by YoKey on 16/4/19.
 */
open class SwipeBackFragment : SupportFragment(), ISwipeBackFragment {
    internal val mDelegate = SwipeBackFragmentDelegate(this)

    override val swipeBackLayout: SwipeBackLayout
        get() = mDelegate.swipeBackLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDelegate.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDelegate.onViewCreated(view, savedInstanceState)
    }

    override fun attachToSwipeBack(view: View): View {
        return mDelegate.attachToSwipeBack(view)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        mDelegate.onHiddenChanged(hidden)
    }

    /**
     * 是否可滑动
     *
     * @param enable
     */
    override fun setSwipeBackEnable(enable: Boolean) {
        mDelegate.setSwipeBackEnable(enable)
    }

    override fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel) {
        mDelegate.setEdgeLevel(edgeLevel)
    }

    override fun setEdgeLevel(widthPixel: Int) {
        mDelegate.setEdgeLevel(widthPixel)
    }

    /**
     * Set the offset of the parallax slip.
     */
    override fun setParallaxOffset(@FloatRange(from = 0.0, to = 1.0) offset: Float) {
        mDelegate.setParallaxOffset(offset)
    }

    override fun onDestroyView() {
        mDelegate.onDestroyView()
        super.onDestroyView()
    }
}