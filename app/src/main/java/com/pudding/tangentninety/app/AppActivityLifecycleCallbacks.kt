package com.pudding.tangentninety.app

import android.app.Activity
import android.app.Application
import android.os.Bundle

/**
 * This created by Error on 2018/03/27,15:17.
 */

abstract class AppActivityLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
}
