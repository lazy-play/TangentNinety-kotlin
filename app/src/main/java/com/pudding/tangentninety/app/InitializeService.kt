package com.pudding.tangentninety.app

import android.app.IntentService
import android.content.Context
import android.content.Intent

import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.pudding.tangentninety.utils.CrashHandler


/**
 * Created by Error on 2017/6/22 0022.
 */

class InitializeService : IntentService("InitializeService") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_INIT == action) {
                initApplication()
            }
        }
    }

    private fun initApplication() {
        val formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
                .methodCount(0)
                .build()
        Logger.addLogAdapter(AndroidLogAdapter(formatStrategy))
        CrashHandler.getInstance(applicationContext)
    }

    companion object {

        private const val ACTION_INIT = "initApplication"

        fun start(context: Context) {
            val intent = Intent(context, InitializeService::class.java)
            intent.action = ACTION_INIT
            context.startService(intent)
        }
    }
}
