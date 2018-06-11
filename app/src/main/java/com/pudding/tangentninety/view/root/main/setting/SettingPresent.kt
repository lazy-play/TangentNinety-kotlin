package com.pudding.tangentninety.view.root.main.setting

import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.event.NoPicEvent
import com.pudding.tangentninety.utils.GlideCacheUtil

import org.greenrobot.eventbus.EventBus

import javax.inject.Inject

/**
 * Created by Error on 2017/6/30 0030.
 */

class SettingPresent @Inject
constructor(private val mDataManager: DataManager) : RxPresenter<SettingContract.View>(), SettingContract.Presenter {


    override val isNoPic: Boolean
        get() = mDataManager.noImageState

    override val isBigFont: Boolean
        get() = mDataManager.bigFontState

    override val isAutoCache: Boolean
        get() = mDataManager.autoCacheState

    override val cacheSize: String
        get() = GlideCacheUtil.getCacheSize()

    override fun setNoPicState(state: Boolean) {
        mDataManager.noImageState = state
        EventBus.getDefault().post(NoPicEvent())
    }

    override fun setBigFontState(state: Boolean) {
        mDataManager.bigFontState = state
    }

    override fun setAutoCacheState(state: Boolean) {
        mDataManager.autoCacheState = state
    }

    override fun clearCache() {
        GlideCacheUtil.clearImageAllCache(App.instance)
        mDataManager.clearCache()
    }
}
