package com.pudding.tangentninety.weight

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

import com.pudding.tangentninety.R
import com.pudding.tangentninety.utils.UIUtil

/**
 * Created by Error on 2017/7/25 0025.
 */

class ExpandableTextView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs), View.OnClickListener {

    /*内容textview*/
    private lateinit var mTvContent: TextView

    /*展开收起textview*/
    private lateinit var mTvExpandCollapse: TextView

    /*是否有重新绘制*/
    private var mRelayout: Boolean = false

    /*默认收起*/
    private var mCollapsed = true

    /*动画执行时间*/
    private var mAnimationDuration: Int = 0
    /*是否正在执行动画*/
    private var mAnimating: Boolean = false
    /* 展开收起状态回调 */
    private var mListener: OnExpandStateChangeListener? = null
    /* listview等列表情况下保存每个item的收起/展开状态 */
    private var mCollapsedStatus: SparseBooleanArray? = null
    /* 列表位置 */
    private var mPosition: Int = 0

    /*设置内容最大行数，超过隐藏*/
    private var mMaxCollapsedLines: Int = 0

    /*这个linerlayout容器的高度*/
    private var mCollapsedHeight: Int = 0

    /*内容tv真实高度（含padding）*/
    private var mTextHeightWithMaxLines: Int = 0

    /*内容tvMarginTopAmndBottom高度*/
    private var mMarginBetweenTxtAndBottom: Int = 0

    /*内容颜色*/
    private var contentTextColor: Int = 0
    /*收起展开颜色*/
    private var collapseExpandTextColor: Int = 0
    /*内容字体大小*/
    private var contentTextSize: Float = 0.toFloat()
    /*收起展字体大小*/
    private var collapseExpandTextSize: Float = 0.toFloat()
    /*收起文字*/
    private var textCollapse: String? = null
    /*展开文字*/
    private var textExpand: String? = null

    /**
     * 获取内容
     * @return
     */
    /**
     * 设置内容
     */
    var text: CharSequence
        get() = mTvContent.text
        set(text) {
            mRelayout = true
            mTvContent.text = text
            visibility = if (TextUtils.isEmpty(text)) View.GONE else View.VISIBLE
        }

    //    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
//    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
//        init(attrs)
//    }

    override fun setOrientation(orientation: Int) {
        if (LinearLayout.HORIZONTAL == orientation) {
            throw IllegalArgumentException("ExpandableTextView only supports Vertical Orientation.")
        }
        super.setOrientation(orientation)
    }

    /**
     * 初始化属性
     * @param attrs
     */
    private fun init(attrs: AttributeSet?) {
        mCollapsedStatus = SparseBooleanArray()

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandTextView)
        mMaxCollapsedLines = typedArray.getInt(R.styleable.ExpandTextView_maxCollapsedLines, MAX_COLLAPSED_LINES)
        mAnimationDuration = typedArray.getInt(R.styleable.ExpandTextView_animDuration, DEFAULT_ANIM_DURATION)

        textCollapse = typedArray.getString(R.styleable.ExpandTextView_textCollapse)
        textExpand = typedArray.getString(R.styleable.ExpandTextView_textExpand)

        if (TextUtils.isEmpty(textCollapse)) {
            textCollapse = resources.getString(R.string.collapse)
        }
        if (TextUtils.isEmpty(textExpand)) {
            textExpand = resources.getString(R.string.expand)
        }
        contentTextColor = typedArray.getColor(R.styleable.ExpandTextView_contentTextColor, ContextCompat.getColor(context, R.color.centerGrayColor))
        contentTextSize = typedArray.getDimension(R.styleable.ExpandTextView_contentTextSize, UIUtil.sp2px(context, 14f).toFloat())

        collapseExpandTextColor = typedArray.getColor(R.styleable.ExpandTextView_collapseExpandTextColor, ContextCompat.getColor(context, R.color.centerGrayColor))
        collapseExpandTextSize = typedArray.getDimension(R.styleable.ExpandTextView_collapseExpandTextSize, UIUtil.sp2px(context, 14f).toFloat())

        //        grarity = typedArray.getInt(R.styleable.ExpandTextView_collapseExpandGrarity, Gravity.CENTER);

        typedArray.recycle()
        // enforces vertical orientation
        orientation = LinearLayout.VERTICAL
        // default visibility is gone
        visibility = View.GONE
    }

    /**
     * 渲染完成时初始化view
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        findViews()
    }

    /**
     * 初始化viwe
     */
    private fun findViews() {
        val inflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.item_expand_collapse, this)
        mTvContent = findViewById<View>(R.id.expandable_text) as TextView
        mTvContent.setOnClickListener(this)
        mTvExpandCollapse = findViewById<View>(R.id.expand_collapse) as TextView
        mTvExpandCollapse.text = if (mCollapsed) resources.getString(R.string.expand) else resources.getString(R.string.collapse)
        mTvExpandCollapse.setOnClickListener(this)

        mTvContent.setTextColor(contentTextColor)
        mTvContent.paint.textSize = contentTextSize

        mTvExpandCollapse.setTextColor(collapseExpandTextColor)
        mTvExpandCollapse.paint.textSize = collapseExpandTextSize
        //
        //        //设置收起展开位置：左或者右
        //        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        //        lp.gravity = grarity;
        //        mTvExpandCollapse.setLayoutParams(lp);
    }

    /**
     * 点击事件
     * @param view
     */
    override fun onClick(view: View) {
        if (mTvExpandCollapse.visibility != View.VISIBLE) {
            return
        }
        mCollapsed = !mCollapsed
        //修改收起/展开图标、文字
        mTvExpandCollapse.text = if (mCollapsed) resources.getString(R.string.expand) else resources.getString(R.string.collapse)
        //保存位置状态
            mCollapsedStatus?.put(mPosition, mCollapsed)
        // 执行展开/收起动画
        mAnimating = true
        val valueAnimator: ValueAnimator = if (mCollapsed) {
            //            mTvContent.setMaxLines(mMaxCollapsedLines);
            ValueAnimator.ofInt(height, mCollapsedHeight)
        } else {
            ValueAnimator.ofInt(height, height + mTextHeightWithMaxLines - mTvContent.height)
        }
        valueAnimator.addUpdateListener {
            val animatedValue = it.animatedValue as Int
            mTvContent.maxHeight = animatedValue - mMarginBetweenTxtAndBottom
            layoutParams.height = animatedValue
            requestLayout()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {

            }

            override fun onAnimationEnd(animator: Animator) {
                // 动画结束后发送结束的信号
                /// clear the animation flag
                mAnimating = false
                // notify the listener
                mListener?.onExpandStateChanged(mTvContent, !mCollapsed)
            }

            override fun onAnimationCancel(animator: Animator) {

            }

            override fun onAnimationRepeat(animator: Animator) {

            }
        })
        valueAnimator.duration = mAnimationDuration.toLong()
        valueAnimator.start()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // 当动画还在执行状态时，拦截事件，不让child处理
        return mAnimating
    }

    /**
     * 重新测量
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // If no change, measure and return
        if (!mRelayout || visibility == View.GONE) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        mRelayout = false

        // Setup with optimistic case
        // i.e. Everything fits. No button needed
        mTvExpandCollapse.visibility = View.GONE
        mTvContent.maxLines = Integer.MAX_VALUE

        // Measure
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //如果内容真实行数小于等于最大行数，不处理
        if (mTvContent.lineCount <= mMaxCollapsedLines) {
            return
        }
        // 获取内容tv真实高度（含padding）
        mTextHeightWithMaxLines = getRealTextViewHeight(mTvContent)

        // 如果是收起状态，重新设置最大行数
        if (mCollapsed) {
            mTvContent.maxLines = mMaxCollapsedLines
        }
        mTvExpandCollapse.visibility = View.VISIBLE

        // Re-measure with new setup
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (mCollapsed) {
            // Gets the margin between the TextView's bottom and the ViewGroup's bottom
            mTvContent.post { mMarginBetweenTxtAndBottom = height - mTvContent.height }
            // 保存这个容器的测量高度
            mCollapsedHeight = measuredHeight
        }
    }


    /*********暴露给外部调用方法 */

    /**
     * 设置收起/展开监听
     * @param listener
     */
    fun setOnExpandStateChangeListener(listener: OnExpandStateChangeListener) {
        mListener = listener
    }

    /**
     * 设置内容，列表情况下，带有保存位置收起/展开状态
     * @param text
     * @param position
     */
    fun setText(text: CharSequence, position: Int) {
        mPosition = position
        //获取状态，如无，默认是true:收起
        mCollapsed = mCollapsedStatus!!.get(position, true)
        clearAnimation()
        //设置收起/展开图标和文字
        mTvExpandCollapse.text = if (mCollapsed) resources.getString(R.string.expand) else resources.getString(R.string.collapse)

        this.text = text
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        requestLayout()
    }

    /**
     * 定义状态改变接口
     */
    interface OnExpandStateChangeListener {
        /**
         * @param textView   - TextView being expanded/collapsed
         * @param isExpanded - true if the TextView has been expanded
         */
        fun onExpandStateChanged(textView: TextView?, isExpanded: Boolean)
    }

    companion object {

        /* 默认最高行数 */
        private const val MAX_COLLAPSED_LINES = 5

        /* 默认动画执行时间 */
        private const val DEFAULT_ANIM_DURATION = 200

        /**
         * 获取内容tv真实高度（含padding）
         * @param textView
         * @return
         */
        private fun getRealTextViewHeight(textView: TextView): Int {
            val textHeight = textView.layout.getLineTop(textView.lineCount)
            val padding = textView.compoundPaddingTop + textView.compoundPaddingBottom
            return textHeight + padding
        }
    }

    init {
        init(attrs)
    }
}//    /*收起展开位置，默认左边*/
//    private int grarity;
