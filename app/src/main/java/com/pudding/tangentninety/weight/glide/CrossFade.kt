package com.pudding.tangentninety.weight.glide

import android.graphics.drawable.Drawable

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.NoTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory

/**
 * Created by Error on 2018/03/12/17:00.
 */

class CrossFade : TransitionFactory<Drawable> {
    override fun build(dataSource: DataSource, isFirstResource: Boolean): Transition<Drawable> {
        return if (dataSource == DataSource.REMOTE)
            DrawableCrossFadeTransition(300, true)
        else
            NoTransition.get()
    }
}
