package com.pudding.tangentninety.view.splash

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.FloatEvaluator
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout

import com.pudding.tangentninety.BuildConfig
import com.pudding.tangentninety.R
import com.pudding.tangentninety.app.App

import butterknife.BindView
import com.pudding.tangentninety.base.BaseActivity
import com.pudding.tangentninety.module.bean.DailyNewListBean
import com.pudding.tangentninety.view.root.RootActivity

/**
 * Created by Error on 2017/6/22 0022.
 */

class SplashActivity : BaseActivity<SplashContract.View, SplashPresent>(), SplashContract.View {

    private var bean: DailyNewListBean? = null
    override val layout: Int = R.layout.activity_splash

    companion object {

        private const val PATH_TIME = 1500
        private const val BACKGROUND_TIME = 1000
    }

    private lateinit var handler: Handler
    @BindView(R.id.logo_stroke_view)
    lateinit var logoStrokeView: ImageView
    @BindView(R.id.logo_view)
    lateinit var logoView: ImageView
    @BindView(R.id.background)
    lateinit var rootLayout: RelativeLayout

    private var onstop: Boolean = false
    private var animOver: Boolean = false
    private var hasPermission: Boolean = false
    private var hasStart: Boolean = false

    private var isExit: Boolean = false

    override fun onStop() {
        super.onStop()
        onstop = true
    }

    override fun onStart() {
        super.onStart()
        if (onstop) {
            onstop = false
            startActivity()
        }
    }
    override fun initInject() {
        activityComponent.inject(this)
    }

    override fun loadDailyNewSuccess(bean: DailyNewListBean?) {
        this.bean=bean
    }

    override fun initEventAndData(savedInstanceState: Bundle?) {
        handler = Handler()
        val set = AnimatorSet()
        val logoStrokeAnimator = logoStrokeView.drawable as AnimatedVectorDrawable
        logoStrokeAnimator.start()
        logoStrokeView.visibility = View.VISIBLE
        val logoAlpha = ObjectAnimator.ofObject(logoView, "alpha", FloatEvaluator(), 0f, 1f)
        logoAlpha.duration = BACKGROUND_TIME.toLong()
        val isnightMode = App.appComponent.preferencesHelper().nightModeState
        val backgroundColor = ObjectAnimator.ofObject(rootLayout, "backgroundColor", ArgbEvaluator(), Color.BLACK, ContextCompat.getColor(this, if (isnightMode) R.color.colorMain_Night else R.color.colorMain))
        backgroundColor.duration = BACKGROUND_TIME.toLong()
        set.playTogether(logoAlpha, backgroundColor)
        set.startDelay = PATH_TIME.toLong()
        set.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (ContextCompat.checkSelfPermission(App.instance,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@SplashActivity,
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            BuildConfig.VERSION_CODE)
                } else {
                    hasPermission = true
                }
                animOver = true
                startActivity()
            }
        })
        set.start()
        mPresenter.loadDailyNewList()
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        hasPermission = true
        startActivity()
    }

    private fun startActivity() {
        if (onstop || !animOver || !hasPermission || hasStart) {
            return
        }
        hasStart = true
        val intent=Intent(this@SplashActivity, RootActivity::class.java)
        intent.putExtra("DailyNewListBean",bean)
        startActivity(intent)
        finish()
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out)
    }

    override fun onBackPressedSupport() {
        exitByDoubleClick()
    }

    private fun exitByDoubleClick() {
        if (!isExit) {
            isExit = true
            handler.postDelayed({
                isExit = false//取消退出
            }, 2000)
        } else {
            App.instance.exitApp()
        }
    }

}