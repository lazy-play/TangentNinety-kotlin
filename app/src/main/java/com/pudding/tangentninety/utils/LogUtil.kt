package com.pudding.tangentninety.utils

import com.orhanobut.logger.Logger
import com.pudding.tangentninety.BuildConfig

/**
 * Created by Error on 2017/6/22 0022.
 */
object LogUtil {

    private val isDebug = BuildConfig.DEBUG

    fun e(throwable: Throwable?, o: Any) {
        if (isDebug) {
            Logger.e(throwable, o.toString(),"")
        }
    }

    fun e(o: Any) {
        LogUtil.e(null, o)
    }

    fun w(o: Any) {
        if (isDebug) {
            Logger.w(o.toString())
        }
    }


    fun d(msg: String) {
        if (isDebug) {
            Logger.d(msg)
        }
    }

    fun i(msg: String) {
        if (isDebug) {
            Logger.i(msg)
        }
    }
}
