package com.pudding.tangentninety.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.widget.Toast

import com.pudding.tangentninety.app.App

import java.io.PrintWriter
import java.io.StringWriter
import java.lang.Thread.UncaughtExceptionHandler
import java.util.HashMap

/**
 * 崩溃时写入日志工具类
 * @author pys
 */
class CrashHandler private constructor(private val context: Context) : UncaughtExceptionHandler {
    // 系统默认的UncaughtException处理类
    private val mDefaultHandler: UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()
    // 用来存储设备信息和异常信息
    private val infos = HashMap<String, String>()

    init {
        // 获取系统默认的UncaughtException处理器
        // 设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex)
        } else {
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
                LogUtil.e(e)
            }

            // 退出程序
            App.instance.exitApp()
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false.
     */
    private fun handleException(ex: Throwable?): Boolean {
        if (ex == null) {
            return false
        }
        // 使用Toast来显示异常信息
        Thread(Runnable {
            Looper.prepare()
            Toast.makeText(context, "程序异常，即将爆破。", Toast.LENGTH_LONG).show()
            Looper.loop()
        }).start()

        // 收集设备参数信息
        collectDeviceInfo(context)
        // 保存日志文件
        saveCrashInfo2File(ex)
        return true
    }

    /**
     * 收集设备参数信息并存储到文件中
     *
     * @param ctx
     */

    fun collectDeviceInfo(ctx: Context) {
        try {
            val pm = ctx.packageManager
            val pi = pm.getPackageInfo(ctx.packageName,
                    PackageManager.GET_ACTIVITIES)
            if (pi != null) {
                val versionName = if (pi.versionName == null)
                    "null"
                else
                    pi.versionName
                infos["versionName"] = versionName
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LogUtil.e(e)
        }

    }

    /**
     * 保存错误信息到文件中
     *
     * @param ex
     * @return 返回文件名称,便于将文件传送到服务器
     */
    private fun saveCrashInfo2File(ex: Throwable) {
        val sb = StringBuffer("====================Start====================\n")
        for ((key, value) in infos) {
            sb.append("$key=$value\n")
        }
        sb.append("Cause by :\n")
        val writer = StringWriter()
        val printWriter = PrintWriter(writer)
        ex.printStackTrace(printWriter)
        var cause: Throwable? = ex.cause
        while (cause != null) {
            cause.printStackTrace(printWriter)
            cause = cause.cause
        }
        printWriter.close()
        val result = writer.toString()
        LogUtil.e(result)
        sb.append(result)
        sb.append("====================End====================\n")
        // 存储日志，可直接上送服务器，也可如下存入文件中
        SystemUtil.writeToLocaleFile(sb.toString())
    }

    companion object {

        //    private static CrashHandler instance;
        private var hasInit: Boolean = false

        @Synchronized
        fun getInstance(context: Context) {
            if (!hasInit) {
                CrashHandler(context)
                hasInit = true
            }
        }
    }

}
