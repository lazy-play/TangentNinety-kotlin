package com.pudding.tangentninety.weight

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

/**
 * Created by Error on 2017/7/23 0023.
 */

class DrawerRecyclerView(context: Context, attrs: AttributeSet?) : RecyclerView(context, attrs) {

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN && ev.x <= width * 0.03f) {
            stopScroll()
            return false
        }
        return super.dispatchTouchEvent(ev)
    }
}
