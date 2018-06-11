package com.pudding.tangentninety.di.module

import android.app.Activity
import android.support.v4.app.Fragment

import com.pudding.tangentninety.di.scope.FragmentScope

import dagger.Module
import dagger.Provides

/**
 * Created by Error on 2017/6/22 0022.
 */

@Module
class FragmentModule(private val fragment: Fragment) {

    @Provides
    @FragmentScope
    fun provideActivity(): Activity? {
        return fragment.activity
    }
}