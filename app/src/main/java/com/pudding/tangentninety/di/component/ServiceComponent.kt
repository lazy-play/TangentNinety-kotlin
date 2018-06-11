package com.pudding.tangentninety.di.component

import android.app.Service

import com.pudding.tangentninety.di.module.ServiceModule
import com.pudding.tangentninety.di.scope.ServiceScope
import com.pudding.tangentninety.view.root.main.download.DownloadService

import dagger.Component

/**
 * Created by Error on 2017/6/22 0022.
 */

@ServiceScope
@Component(dependencies = [(AppComponent::class)], modules = [(ServiceModule::class)])
interface ServiceComponent {

    val service: Service
    fun inject(downloadService: DownloadService)
}
