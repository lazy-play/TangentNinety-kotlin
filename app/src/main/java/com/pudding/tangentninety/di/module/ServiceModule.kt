package com.pudding.tangentninety.di.module

import android.app.Service

import com.pudding.tangentninety.di.scope.ServiceScope

import dagger.Module
import dagger.Provides

/**
 * Created by Error on 2017/6/22 0022.
 */

@Module
class ServiceModule(private val mService: Service) {

    @Provides
    @ServiceScope
    fun provideService(): Service {
        return mService
    }
}