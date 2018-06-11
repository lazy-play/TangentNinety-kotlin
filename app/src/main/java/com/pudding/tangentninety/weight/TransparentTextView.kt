package com.pudding.tangentninety.weight

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet

/**
 * Created by Error on 2017/7/12 0012.
 */

class TransparentTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet?=null,styleID:Int=0) : AppCompatTextView(context, attrs,styleID) {
    private var mMaskBitmap: Bitmap? = null
    private var mMaskCanvas: Canvas? = null
    private lateinit var mPaint: Paint
    private var mBackground: Drawable? = null
    private var mBackgroundBitmap: Bitmap? = null
    private var mBackgroundCanvas: Canvas? = null

    private val isNothingToDraw: Boolean
        get() = (mBackground == null
                || width == 0
                || height == 0)


    private fun init() {
        mPaint = Paint()
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        super.setTextColor(Color.BLACK)
        super.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    override fun setBackgroundDrawable(bg: Drawable) {
        if (mBackground === bg) {
            return
        }

        mBackground = bg

        // Will always draw drawable using view bounds. This might be a
        // problem if the drawable should force the view to be bigger, e.g.
        // the view sets its dimensions to wrap_content and the drawable
        // is larger than the text.
        val w = width
        val h = height
        if (mBackground != null && w != 0 && h != 0) {
            mBackground!!.setBounds(0, 0, w, h)
        }
        requestLayout()
        invalidate()
    }

    @Suppress("DEPRECATION")
    override fun setBackgroundColor(color: Int) {
        setBackgroundDrawable(ColorDrawable(color))
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || h == 0) {
            freeBitmaps()
            return
        }

        createBitmaps(w, h)
        if (mBackground != null) {
            mBackground!!.setBounds(0, 0, w, h)
        }
    }

    private fun createBitmaps(w: Int, h: Int) {
        mBackgroundBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mBackgroundCanvas = Canvas(mBackgroundBitmap!!)
        mMaskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ALPHA_8)
        mMaskCanvas = Canvas(mMaskBitmap!!)
    }

    private fun freeBitmaps() {
        mBackgroundBitmap = null
        mBackgroundCanvas = null
        mMaskBitmap = null
        mMaskCanvas = null
    }

    override fun onDraw(canvas: Canvas) {
        if (isNothingToDraw) {
            return
        }
        drawMask()
        drawBackground()
        canvas.drawBitmap(mBackgroundBitmap!!, 0f, 0f, null)
    }

    // draw() calls onDraw() leading to stack overflow
    @SuppressLint("WrongCall")
    private fun drawMask() {
        clear(mMaskCanvas!!)
        super.onDraw(mMaskCanvas)
    }

    private fun drawBackground() {
        clear(mBackgroundCanvas!!)
        mBackground!!.draw(mBackgroundCanvas!!)
        mBackgroundCanvas!!.drawBitmap(mMaskBitmap!!, 0f, 0f, mPaint)
    }

    private fun clear(canvas: Canvas) {
        canvas.drawColor(Color.BLACK, PorterDuff.Mode.CLEAR)
    }

    init {
        init()
    }
}