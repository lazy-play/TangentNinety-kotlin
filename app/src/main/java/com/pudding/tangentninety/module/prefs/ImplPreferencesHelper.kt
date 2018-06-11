package com.pudding.tangentninety.module.prefs

import android.content.Context
import android.content.SharedPreferences

import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.app.Constants

import javax.inject.Inject

/**
 * Created by Error on 2017/6/22 0022.
 */

class ImplPreferencesHelper @Inject
constructor() : PreferencesHelper {

    private val mSPrefs: SharedPreferences

    override var nightModeState: Boolean
        get() = mSPrefs.getBoolean(Constants.SP_NIGHT_MODE, DEFAULT_NIGHT_MODE)
        set(state) = mSPrefs.edit().putBoolean(Constants.SP_NIGHT_MODE, state).apply()

    override var noImageState: Boolean
        get() = mSPrefs.getBoolean(Constants.SP_NO_IMAGE, DEFAULT_NO_IMAGE)
        set(state) = mSPrefs.edit().putBoolean(Constants.SP_NO_IMAGE, state).apply()

    override var autoCacheState: Boolean
        get() = mSPrefs.getBoolean(Constants.SP_AUTO_CACHE, DEFAULT_AUTO_SAVE)
        set(state) = mSPrefs.edit().putBoolean(Constants.SP_AUTO_CACHE, state).apply()

    override var bigFontState: Boolean
        get() = mSPrefs.getBoolean(Constants.SP_BIG_FONT, DEFAULT_BIG_FONT)
        set(state) = mSPrefs.edit().putBoolean(Constants.SP_BIG_FONT, state).apply()

    init {
        val app = App.instance

        mSPrefs = app.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    companion object {

        private const val DEFAULT_NIGHT_MODE = false
        private const val DEFAULT_NO_IMAGE = false
        private const val DEFAULT_AUTO_SAVE = true
        private const val DEFAULT_BIG_FONT = false

        private const val SHAREDPREFERENCES_NAME = "my_sp"
    }

}