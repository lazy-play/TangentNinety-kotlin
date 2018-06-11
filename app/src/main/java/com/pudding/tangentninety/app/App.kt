package com.pudding.tangentninety.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager

import com.pudding.tangentninety.di.component.AppComponent
import com.pudding.tangentninety.di.component.DaggerAppComponent
import com.pudding.tangentninety.di.module.AppModule
import com.pudding.tangentninety.di.module.HttpModule
import com.pudding.tangentninety.utils.toast
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

import java.util.ArrayList
import java.util.Collections


/**
 * Created by Error on 2017/6/22 0022.
 */

class App : Application() {
    private lateinit var activityList: MutableList<Activity>

    override fun onCreate() {
        super.onCreate()
        instance = this
//        Fragmentation.builder()
//                // 显示悬浮球 ; 其他Mode:SHAKE: 摇一摇唤出   NONE：隐藏
//                .stackViewMode(Fragmentation.BUBBLE)
//                .debug(true)
//                .install()
        activityList = Collections.synchronizedList(ArrayList())
        registerActivityLifecycleCallbacks(object : AppActivityLifecycleCallbacks() {
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
                activityList.add(activity)
            }

            override fun onActivityDestroyed(activity: Activity) {
                activityList.remove(activity)
            }
        })
        //初始化屏幕宽高
        getScreenSize()
        //初始化数据库
        //        sRefWatcher = LeakCanary.install(this);
        //在子线程中完成其他初始化
        InitializeService.start(this)
    }

    //    public static RefWatcher getRefWatcher() {
    //        return sRefWatcher;
    //    }
    fun exitApp() {
        val activityIterator = activityList.iterator()
        while (activityIterator.hasNext()) {
            val activity = activityIterator.next()
            activity.finish()
            activityIterator.remove()
        }
        activityList.clear()
        System.exit(0)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    private fun getScreenSize() {
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        val display = windowManager.defaultDisplay
        display.getMetrics(dm)
        DIMEN_RATE = dm.density
        DIMEN_DPI = dm.densityDpi.toFloat()
        SCREEN_WIDTH=dm.widthPixels.toFloat()
        SCREEN_HEIGHT=dm.heightPixels.toFloat()
    }

    fun toastMessage(message: String) {
        Flowable.just(message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({toast(message)})
    }

    companion object {
        lateinit var instance: App
        private set
        val appComponent: AppComponent by lazy{
            DaggerAppComponent.builder()
                .appModule(AppModule(instance))
                .httpModule(HttpModule())
                .build()
        }
        var SCREEN_WIDTH = -1f
        var SCREEN_HEIGHT = -1f
        var DIMEN_RATE = -1.0f
        var DIMEN_DPI = -1f
    }
}
