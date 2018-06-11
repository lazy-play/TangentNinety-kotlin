package com.pudding.tangentninety.weight

import android.content.Context
import android.support.design.widget.NavigationView
import android.support.v4.view.ViewCompat
import android.util.AttributeSet

/**
 * Created by Error on 2017/6/26 0026.
 */

class InsetNavigationView(context: Context, attrs: AttributeSet?) : NavigationView(context, attrs) {
    init {
        ViewCompat.setOnApplyWindowInsetsListener(this) { _, insets -> insets }
    }
}
