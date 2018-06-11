package com.pudding.tangentninety.base

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.Toolbar

import com.pudding.tangentninety.R
import com.pudding.tangentninety.app.App

import butterknife.ButterKnife
import butterknife.Unbinder
import me.yokeyword.fragmentation.SupportActivity

/**
 * Created by Error on 2017/6/22 0022.
 */

abstract class SimpleActivity : SupportActivity() {

    protected lateinit var mContext: Activity
    private lateinit var mUnBinder: Unbinder

    protected abstract val layout: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        setDayNightTheme()
        super.onCreate(savedInstanceState)
        setContentView(layout)
        mUnBinder = ButterKnife.bind(this)
        mContext = this
        onViewCreated()
        initEventAndData(savedInstanceState)
    }

    protected open fun setDayNightTheme() {
        if (!App.appComponent.preferencesHelper().nightModeState) {
            setTheme(R.style.AppTheme)
        } else {
            setTheme(R.style.AppTheme_Night)
        }
    }

    fun setToolBar(toolbar: Toolbar?, homeButtonEnable: Boolean) {
        setSupportActionBar(toolbar)
        if (toolbar == null)
            return
        if (homeButtonEnable) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
            toolbar.setNavigationOnClickListener { onBackPressedSupport() }
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }
    }

    protected open fun onViewCreated() {

    }

    override fun onDestroy() {
        super.onDestroy()
        mUnBinder.unbind()
    }

    protected abstract fun initEventAndData(savedInstanceState: Bundle?)
}
