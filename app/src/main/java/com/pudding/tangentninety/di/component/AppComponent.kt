package com.pudding.tangentninety.di.component


import com.baidu.location.LocationClient
import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.di.module.AppModule
import com.pudding.tangentninety.di.module.HttpModule
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.db.RoomHelper
import com.pudding.tangentninety.module.http.RetrofitHelper
import com.pudding.tangentninety.module.prefs.ImplPreferencesHelper

import javax.inject.Singleton

import dagger.Component

/**
 * Created by Error on 2017/6/22 0022.
 */

@Singleton
@Component(modules = [(AppModule::class), (HttpModule::class)])
interface AppComponent {

    val context: App  // 提供App的Context

    val dataManager: DataManager //数据中心

    fun retrofitHelper(): RetrofitHelper   //提供http的帮助类

    fun roomHelper(): RoomHelper     //提供数据库帮助类

    fun preferencesHelper(): ImplPreferencesHelper  //提供sp帮助类

    fun baiduLocationClient(): LocationClient
}
