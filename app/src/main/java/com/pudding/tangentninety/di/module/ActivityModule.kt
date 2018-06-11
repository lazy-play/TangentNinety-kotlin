package com.pudding.tangentninety.di.module

import android.app.Activity

import com.pudding.tangentninety.di.scope.ActivityScope

import dagger.Module
import dagger.Provides

/**
 * Created by Error on 2017/6/22 0022.
 */

@Module
class ActivityModule(private val mActivity: Activity) {

    @Provides
    @ActivityScope
    fun provideActivity(): Activity {
        return mActivity
    }
}