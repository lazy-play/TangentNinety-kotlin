package com.pudding.tangentninety.di.module

import com.baidu.location.LocationClient
import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.db.DBHelper
import com.pudding.tangentninety.module.db.RoomHelper
import com.pudding.tangentninety.module.http.HttpHelper
import com.pudding.tangentninety.module.http.RetrofitHelper
import com.pudding.tangentninety.module.prefs.ImplPreferencesHelper
import com.pudding.tangentninety.module.prefs.PreferencesHelper

import javax.inject.Singleton

import dagger.Module
import dagger.Provides

/**
 * Created by Error on 2017/6/22 0022.
 */

@Module
class AppModule(private val application: App) {

    @Provides
    @Singleton
    internal fun provideApplicationContext(): App {
        return application
    }

    @Provides
    @Singleton
    internal fun provideHttpHelper(retrofitHelper: RetrofitHelper): HttpHelper {
        return retrofitHelper
    }

    @Provides
    @Singleton
    internal fun provideDBHelper(roomHelper: RoomHelper): DBHelper {
        return roomHelper
    }

    @Provides
    @Singleton
    internal fun providePreferencesHelper(implPreferencesHelper: ImplPreferencesHelper): PreferencesHelper {
        return implPreferencesHelper
    }

    @Provides
    @Singleton
    internal fun provideDataManager(httpHelper: HttpHelper, DBHelper: DBHelper, preferencesHelper: PreferencesHelper): DataManager {
        return DataManager(httpHelper, DBHelper, preferencesHelper)
    }

    @Provides
    @Singleton
    internal fun provideLocationClient(): LocationClient {
        return LocationClient(application)
    }
}
