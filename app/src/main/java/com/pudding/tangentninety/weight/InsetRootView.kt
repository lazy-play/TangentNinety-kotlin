package com.pudding.tangentninety.weight

import android.content.Context
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.FrameLayout

import com.pudding.tangentninety.module.event.TopBottomPuddingChange

import org.greenrobot.eventbus.EventBus

/**
 * This created by Error on 2018/04/02,11:07.
 */
class InsetRootView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    var bottomPadding = 0
        private set
    var topPadding = 0
        private set

    init {
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            topPadding = insets.systemWindowInsetTop
            bottomPadding = insets.systemWindowInsetBottom
            if (topPadding + bottomPadding != 0) {
                EventBus.getDefault().post(TopBottomPuddingChange())
            }
            insets
        }
    }
}
