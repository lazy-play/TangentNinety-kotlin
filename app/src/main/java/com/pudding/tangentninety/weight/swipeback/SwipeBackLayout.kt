package com.pudding.tangentninety.weight.swipeback

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.IntDef
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentationMagician
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout

import com.pudding.tangentninety.R

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.ArrayList

import me.yokeyword.fragmentation.ISupportFragment

/**
 * Thx https://github.com/ikew0ng/SwipeBackLayout.
 *
 *
 * Created by YoKey on 16/4/19.
 */
class SwipeBackLayout @JvmOverloads constructor(private val mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(mContext, attrs, defStyleAttr) {

    private var mScrollFinishThreshold = DEFAULT_SCROLL_THRESHOLD

    /**
     * Get ViewDragHelper
     */
    var viewDragHelper: BetterViewDragHelper? = null
        private set

    private var mScrollPercent: Float = 0.toFloat()
    private var mScrimOpacity: Float = 0.toFloat()

    private var mActivity: FragmentActivity? = null
    private var mContentView: View? = null
    private var mFragment: ISupportFragment? = null
    private var mPreFragment: Fragment? = null

    private var mShadowLeft: Drawable? = null
    private var mShadowRight: Drawable? = null
    private val mTmpRect = Rect()

    private var mEdgeFlag: Int = 0
    private var mEnable = true
    private var mCurrentSwipeOrientation: Int = 0
    private var mParallaxOffset = DEFAULT_PARALLAX

    private var mCallOnDestroyView: Boolean = false

    private var mInLayout: Boolean = false

    private var mContentLeft: Int = 0
    private var mContentTop: Int = 0

    /**
     * The set of listeners to be sent events through.
     */
    private val mListeners: MutableList<OnSwipeListener> by lazy {ArrayList<OnSwipeListener>()}
    private var canRequestparent: Boolean = false

    enum class EdgeLevel {
        MAX, MIN, MED
    }

    init {
        init()
    }

    private fun init() {
        viewDragHelper = BetterViewDragHelper.create(this, ViewDragCallback())
        setShadow(R.drawable.shadow_left, EDGE_LEFT)
        setEdgeOrientation(EDGE_LEFT)
    }

    /**
     * Set scroll threshold, we will close the activity, when scrollPercent over
     * this value
     *
     * @param threshold
     */
    fun setScrollThresHold(threshold: Float) {
        if (threshold >= 1.0f || threshold <= 0) {
            throw IllegalArgumentException("Threshold value should be between 0 and 1.0")
        }
        mScrollFinishThreshold = threshold
    }

    fun setParallaxOffset(offset: Float) {
        this.mParallaxOffset = offset
    }

    fun setEdgeOrientation(@EdgeOrientation orientation: Int) {
        mEdgeFlag = orientation
        viewDragHelper!!.setEdgeTrackingEnabled(orientation)
    }

    @IntDef(EDGE_LEFT, EDGE_RIGHT, EDGE_ALL)
    @Retention(RetentionPolicy.SOURCE)
    annotation class EdgeOrientation

    /**
     * Set a drawable used for edge shadow.
     */
    fun setShadow(shadow: Drawable, edgeFlag: Int) {
        if (edgeFlag and EDGE_LEFT != 0) {
            mShadowLeft = shadow
        } else if (edgeFlag and EDGE_RIGHT != 0) {
            mShadowRight = shadow
        }
        invalidate()
    }

    /**
     * Set a drawable used for edge shadow.
     */
    fun setShadow(resId: Int, edgeFlag: Int) {
        setShadow(ContextCompat.getDrawable(mContext,resId)!!, edgeFlag)
    }

    /**
     * Add a callback to be invoked when a swipe event is sent to this view.
     *
     * @param listener the swipe listener to attach to this view
     */
    fun addSwipeListener(listener: OnSwipeListener) {
        mListeners.add(listener)
    }

    /**
     * Removes a listener from the set of listeners
     *
     * @param listener
     */
    fun removeSwipeListener(listener: OnSwipeListener) {
        mListeners.remove(listener)
    }

    interface OnSwipeListener {
        /**
         * Invoke when state change
         *
         * @param state flag to describe scroll state
         * @see .STATE_IDLE
         *
         * @see .STATE_DRAGGING
         *
         * @see .STATE_SETTLING
         *
         * @see .STATE_FINISHED
         */
        fun onDragStateChange(state: Int)

        /**
         * Invoke when edge touched
         *
         * @param oritentationEdgeFlag edge flag describing the edge being touched
         * @see .EDGE_LEFT
         *
         * @see .EDGE_RIGHT
         */
        fun onEdgeTouch(oritentationEdgeFlag: Int)

        /**
         * Invoke when scroll percent over the threshold for the first time
         *
         * @param scrollPercent scroll percent of this view
         */
        fun onDragScrolled(scrollPercent: Float)
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        val isDrawView = child === mContentView
        val drawChild = super.drawChild(canvas, child, drawingTime)
        if (isDrawView && mScrimOpacity > 0 && viewDragHelper!!.viewDragState != BetterViewDragHelper.STATE_IDLE) {
            drawShadow(canvas, child)
            drawScrim(canvas, child)
        }
        return drawChild
    }

    private fun drawShadow(canvas: Canvas, child: View) {
        val childRect = mTmpRect
        child.getHitRect(childRect)

        if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
            mShadowLeft!!.setBounds(childRect.left - mShadowLeft!!.intrinsicWidth, childRect.top, childRect.left, childRect.bottom)
            mShadowLeft!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowLeft!!.draw(canvas)
        } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
            mShadowRight!!.setBounds(childRect.right, childRect.top, childRect.right + mShadowRight!!.intrinsicWidth, childRect.bottom)
            mShadowRight!!.alpha = (mScrimOpacity * FULL_ALPHA).toInt()
            mShadowRight!!.draw(canvas)
        }
    }

    private fun drawScrim(canvas: Canvas, child: View) {
        val baseAlpha = (DEFAULT_SCRIM_COLOR and -0x1000000).ushr(24)
        val alpha = (baseAlpha * mScrimOpacity).toInt()
        val color = alpha shl 24

        if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
            canvas.clipRect(0, 0, child.left, height)
        } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
            canvas.clipRect(child.right, 0, right, height)
        }
        canvas.drawColor(color)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mInLayout = true
        if (mContentView != null) {
            mContentView!!.layout(mContentLeft, mContentTop,
                    mContentLeft + mContentView!!.measuredWidth,
                    mContentTop + mContentView!!.measuredHeight)
        }
        mInLayout = false
    }

    override fun requestLayout() {
        if (!mInLayout) {
            super.requestLayout()
        }
    }

    override fun computeScroll() {
        mScrimOpacity = 1 - mScrollPercent
        if (mScrimOpacity >= 0) {
            if (viewDragHelper!!.continueSettling(true)) {
                ViewCompat.postInvalidateOnAnimation(this)
            }

            if (mPreFragment != null && mPreFragment!!.view != null) {
                if (mCallOnDestroyView) {
                    mPreFragment!!.view!!.x = 0f
                    return
                }

                if (viewDragHelper!!.capturedView != null) {
                    val leftOffset = ((viewDragHelper!!.capturedView!!.left - width).toFloat() * mParallaxOffset * mScrimOpacity).toInt()
                    mPreFragment!!.view!!.x = (if (leftOffset > 0) 0 else leftOffset).toFloat()
                }
            }
        }
    }

    /**
     * hide
     */
    fun internalCallOnDestroyView() {
        mCallOnDestroyView = true
    }

    fun setFragment(fragment: ISupportFragment, view: View) {
        this.mFragment = fragment
        mContentView = view
    }

    fun hiddenFragment() {
            mPreFragment?.view?.visibility = View.GONE
    }

    fun attachToActivity(activity: FragmentActivity) {
        mActivity = activity
        val a = activity.theme.obtainStyledAttributes(intArrayOf(android.R.attr.windowBackground))
        val background = a.getResourceId(0, 0)
        a.recycle()

        val decor = activity.window.decorView as ViewGroup
        val decorChild = decor.getChildAt(0) as ViewGroup
        decorChild.setBackgroundResource(background)
        decor.removeView(decorChild)
        addView(decorChild)
        setContentView(decorChild)
        decor.addView(this)
    }

    fun attachToFragment(fragment: ISupportFragment, view: View) {
        addView(view)
        setFragment(fragment, view)
    }

    private fun setContentView(view: View) {
        mContentView = view
    }

    fun setEnableGesture(enable: Boolean) {
        mEnable = enable
    }

    fun setEdgeLevel(edgeLevel: EdgeLevel) {
        validateEdgeLevel(-1, edgeLevel)
    }

    fun setEdgeLevel(widthPixel: Int) {
        validateEdgeLevel(widthPixel, null)
    }

    private fun validateEdgeLevel(widthPixel: Int, edgeLevel: EdgeLevel?) {
        val metrics = DisplayMetrics()
        val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(metrics)
        if (widthPixel >= 0) {
            viewDragHelper!!.edgeSize = widthPixel
        } else {
            when (edgeLevel) {
                EdgeLevel.MAX -> viewDragHelper!!.edgeSize = metrics.widthPixels
                EdgeLevel.MED -> viewDragHelper!!.edgeSize = metrics.widthPixels / 2
                else -> viewDragHelper!!.edgeSize = (20 * metrics.density + 0.5f).toInt()
            }
        }
    }

    private inner class ViewDragCallback : BetterViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            val dragEnable = viewDragHelper!!.isEdgeTouched(mEdgeFlag, pointerId)
            if (dragEnable) {
                if (viewDragHelper!!.isEdgeTouched(EDGE_LEFT, pointerId)) {
                    mCurrentSwipeOrientation = EDGE_LEFT
                } else if (viewDragHelper!!.isEdgeTouched(EDGE_RIGHT, pointerId)) {
                    mCurrentSwipeOrientation = EDGE_RIGHT
                }

                mListeners?.forEach{it.onEdgeTouch(mCurrentSwipeOrientation)}

                if (mPreFragment == null) {
                    if (mFragment != null) {
                        val fragmentList = FragmentationMagician.getActiveFragments((mFragment as Fragment).fragmentManager)
                        if (fragmentList != null && fragmentList.size > 1) {
                            val index = fragmentList.indexOf(mFragment as Fragment)
                            for (i in index - 1 downTo 0) {
                                val fragment = fragmentList[i]
                                if (fragment != null && fragment.view != null) {
                                    fragment.view!!.visibility = View.VISIBLE
                                    mPreFragment = fragment
                                    break
                                }
                            }
                        }
                    }
                } else {
                    val preView = mPreFragment!!.view
                    if (preView != null && preView.visibility != View.VISIBLE) {
                        preView.visibility = View.VISIBLE
                    }
                }
            }
            return dragEnable
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            var ret = 0
            if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
                ret = Math.min(child.width, Math.max(left, 0))
            } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
                ret = Math.min(0, Math.max(left, -child.width))
            }
            return ret
        }

        override fun onViewPositionChanged(changedView: View, left: Int, top: Int, dx: Int, dy: Int) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)

            if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
                mScrollPercent = Math.abs(left.toFloat() / (mContentView!!.width + mShadowLeft!!.intrinsicWidth))
            } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
                mScrollPercent = Math.abs(left.toFloat() / (mContentView!!.width + mShadowRight!!.intrinsicWidth))
            }
            mContentLeft = left
            mContentTop = top
            invalidate()

            if (mListeners != null && viewDragHelper!!.viewDragState == STATE_DRAGGING && mScrollPercent <= 1 && mScrollPercent > 0) {
                mListeners?.forEach { it.onDragScrolled(mScrollPercent) }
            }

            if (mScrollPercent > 1) {
                if (mFragment != null) {
                    if (mCallOnDestroyView) return

                    if (!(mFragment as Fragment).isDetached) {
                        onDragFinished()
                        mFragment!!.supportDelegate.popQuiet()
                    }
                } else {
                    if (!mActivity!!.isFinishing) {
                        onDragFinished()
                        mActivity!!.finish()
                        mActivity!!.overridePendingTransition(0, 0)
                    }
                }
            }
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return if (mFragment != null) {
                1
            } else 0
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            val childWidth = releasedChild.width

            var left = 0
            val top = 0
            if (mCurrentSwipeOrientation and EDGE_LEFT != 0) {
                left = if (xvel > 0 || xvel == 0f && mScrollPercent > mScrollFinishThreshold)
                    childWidth+ mShadowLeft!!.intrinsicWidth + OVERSCROLL_DISTANCE
                else
                    0
            } else if (mCurrentSwipeOrientation and EDGE_RIGHT != 0) {
                left = if (xvel < 0 || xvel == 0f && mScrollPercent > mScrollFinishThreshold)
                    -(childWidth
                            + mShadowRight!!.intrinsicWidth + OVERSCROLL_DISTANCE)
                else
                    0
            }

            viewDragHelper!!.settleCapturedViewAt(left, top)
            invalidate()
        }

        override fun onViewDragStateChanged(state: Int) {
            super.onViewDragStateChanged(state)
            if (mListeners != null) {
                for (listener in mListeners!!) {
                    listener.onDragStateChange(state)
                }
            }
        }

        override fun onEdgeTouched(edgeFlags: Int, pointerId: Int) {
            super.onEdgeTouched(edgeFlags, pointerId)
            if (mEdgeFlag and edgeFlags != 0) {
                mCurrentSwipeOrientation = edgeFlags
            }
        }
    }

    private fun onDragFinished() {
        if (mListeners != null) {
            for (listener in mListeners!!) {
                listener.onDragStateChange(STATE_FINISHED)
            }
        }
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (canRequestparent) {
            super.requestDisallowInterceptTouchEvent(disallowIntercept)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (!mEnable) return super.onInterceptTouchEvent(ev)
        canRequestparent = !(ev.action == MotionEvent.ACTION_DOWN)
        try {
            return viewDragHelper!!.shouldInterceptTouchEvent(ev)
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        return false
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!mEnable) return super.onTouchEvent(event)
        try {
            viewDragHelper!!.processTouchEvent(event)
            return true
        } catch (ignored: Exception) {
            ignored.printStackTrace()
        }

        return false
    }

    companion object {
        /**
         * Edge flag indicating that the left edge should be affected.
         */
        const val EDGE_LEFT = BetterViewDragHelper.EDGE_LEFT

        /**
         * Edge flag indicating that the right edge should be affected.
         */
        const val EDGE_RIGHT = BetterViewDragHelper.EDGE_RIGHT

        const val EDGE_ALL = EDGE_LEFT or EDGE_RIGHT


        /**
         * A view is not currently being dragged or animating as a result of a
         * fling/snap.
         */
        const val STATE_IDLE = BetterViewDragHelper.STATE_IDLE

        /**
         * A view is currently being dragged. The position is currently changing as
         * a result of user input or simulated user input.
         */
        const val STATE_DRAGGING = BetterViewDragHelper.STATE_DRAGGING

        /**
         * A view is currently settling into place as a result of a fling or
         * predefined non-interactive motion.
         */
        const val STATE_SETTLING = BetterViewDragHelper.STATE_SETTLING

        /**
         * A view is currently drag finished.
         */
        const val STATE_FINISHED = 3

        private const val DEFAULT_SCRIM_COLOR = -0x67000000
        private const val DEFAULT_PARALLAX = 0.33f
        private const val FULL_ALPHA = 255
        private const val DEFAULT_SCROLL_THRESHOLD = 0.4f
        private const val OVERSCROLL_DISTANCE = 10
    }
}
