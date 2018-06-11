package com.pudding.tangentninety.weight.swipeback

import android.support.annotation.FloatRange
import android.view.View


/**
 * Created by YoKey on 17/6/29.
 */

interface ISwipeBackFragment {

    val swipeBackLayout: SwipeBackLayout

    fun attachToSwipeBack(view: View): View

    fun setSwipeBackEnable(enable: Boolean)

    fun setEdgeLevel(edgeLevel: SwipeBackLayout.EdgeLevel)

    fun setEdgeLevel(widthPixel: Int)

    /**
     * Set the offset of the parallax slip.
     */
    fun setParallaxOffset(@FloatRange(from = 0.0, to = 1.0) offset: Float)
}
