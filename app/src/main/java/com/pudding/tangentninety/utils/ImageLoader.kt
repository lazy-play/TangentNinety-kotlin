package com.pudding.tangentninety.utils

import android.support.v4.content.ContextCompat
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.Request
import com.pudding.tangentninety.GlideApp
import com.pudding.tangentninety.R
import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.weight.glide.ColorFilterTransformation
import com.pudding.tangentninety.weight.glide.CrossFade
import com.pudding.tangentninety.weight.glide.RoundedCornersTransformation

import java.io.File
import java.lang.ref.WeakReference

object ImageLoader{
    /**
     * （下载）获取图片缓存文件
     */
    fun getGlideCache(url: String): File? {
        try {
            return Glide.with(App.instance)
                    .downloadOnly()
                    .load(url)
                    .submit()
                    .get()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}


fun ImageView.loadUrl(url: String = "",
                      isNopic: Boolean
): Request? {
    val wr = WeakReference(this)
    return wr.get()?.let {
        GlideApp.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .transition(DrawableTransitionOptions.with(CrossFade()))
                .onlyRetrieveFromCache(isNopic)
                .into(it)
                .request
    }
}

fun ImageView.loadUrlWithColorFilter(url: String = "",
                                     isNopic: Boolean
): Request? {
    val wr = WeakReference(this)
    return wr.get()?.let {
        GlideApp.with(context).load(url)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .transition(DrawableTransitionOptions.with(CrossFade()))
                .transform(ColorFilterTransformation(ContextCompat.getColor(context, R.color.colorFilter)))
                .onlyRetrieveFromCache(isNopic)
                .into(it)
                .request
    }
}

fun ImageView.loadUrlRound(url: String = "",
                           isNopic: Boolean,
                           cornerType: RoundedCornersTransformation.CornerType = RoundedCornersTransformation.CornerType.ALL
): Request? {
    val wr = WeakReference(this)
    val placeholder = if (cornerType == RoundedCornersTransformation.CornerType.ALL) R.drawable.loading_image_rectangle else R.drawable.loading_image_square
    return wr.get()?.let {
        GlideApp.with(context)
                .load(url)
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transition(DrawableTransitionOptions.with(CrossFade()))
                .transforms(CenterCrop(), RoundedCornersTransformation((App.DIMEN_RATE * 4).toInt(), 0, cornerType))
                .onlyRetrieveFromCache(isNopic)
                .into(it)
                .request
    }
}

/**
 * 圆形图片（如用户头像）
 */
fun ImageView.loadUrlCircle(url: String = "",
                            isNopic: Boolean) {
    val wr = WeakReference(this)
    wr.get()?.let {
        GlideApp.with(context)
                .load(url)
                .placeholder(R.drawable.loading_image_square)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .transition(DrawableTransitionOptions.with(CrossFade()))
                .transforms(CenterCrop(), CircleCrop())
                .onlyRetrieveFromCache(isNopic)
                .into(it)

    }
}
