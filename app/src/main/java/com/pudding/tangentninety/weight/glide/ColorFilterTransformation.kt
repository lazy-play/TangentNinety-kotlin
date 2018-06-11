package com.pudding.tangentninety.weight.glide

/**
 * This created by Error on 2018/05/17,14:25.
 */
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool

import java.security.MessageDigest

class ColorFilterTransformation(private val color: Int) : BitmapTransformation() {

    override fun transform(context: Context, pool: BitmapPool,
                           toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        val width = toTransform.width
        val height = toTransform.height

        val config = if (toTransform.config != null) toTransform.config else Bitmap.Config.ARGB_8888
        val bitmap = pool.get(width, height, config)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.isAntiAlias = true
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(toTransform, 0f, 0f, paint)

        return bitmap
    }

    override fun toString(): String {
        return "ColorFilterTransformation(color=$color)"
    }

    override fun equals(other: Any?): Boolean {
        return other is ColorFilterTransformation && other.color == color
    }

    override fun hashCode(): Int {
        return ID.hashCode() + color * 10
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update((ID + color).toByteArray(Key.CHARSET))
    }

    companion object {

        private const val VERSION = 1
        private const val ID = "ColorFilterTransformation.$VERSION"
    }
}
