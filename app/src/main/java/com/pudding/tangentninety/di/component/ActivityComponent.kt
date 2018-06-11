package com.pudding.tangentninety.di.component

import android.app.Activity

import com.pudding.tangentninety.di.module.ActivityModule
import com.pudding.tangentninety.di.scope.ActivityScope
import com.pudding.tangentninety.view.root.main.storydetail.photo.PhotoActivity
import com.pudding.tangentninety.view.splash.SplashActivity

import dagger.Component

/**
 * Created by Error on 2017/6/22 0022.
 */

@ActivityScope
@Component(dependencies = [(AppComponent::class)], modules = [(ActivityModule::class)])
interface ActivityComponent {

    val activity: Activity?

    fun inject(photoActivity: PhotoActivity)
    fun inject(splashActivity: SplashActivity)
}
