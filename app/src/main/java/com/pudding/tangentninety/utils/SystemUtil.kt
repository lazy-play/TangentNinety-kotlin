package com.pudding.tangentninety.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Toast

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.pudding.tangentninety.GlideApp
import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.app.Constants

import org.reactivestreams.Publisher

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.nio.channels.FileChannel
import java.text.ParseException
import java.text.SimpleDateFormat
import io.reactivex.Flowable
import io.reactivex.functions.Function
import io.reactivex.subscribers.ResourceSubscriber
import java.util.*

/**
 * Created by Error on 2017/6/22 0022.
 */

object SystemUtil {
    private val formatter = SimpleDateFormat("yyyyMMdd",Locale.CHINA)
    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
    // 获取NetworkInfo对象
    // 判断当前网络状态是否为连接状态
    val isNetworkAvailable: Boolean
        get() {
            val connectivityManager = App.instance.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                val networkInfo = connectivityManager.allNetworks

                if (networkInfo != null && networkInfo.isNotEmpty()) {
                    for (aNetworkInfo in networkInfo) {
                        if (connectivityManager.getNetworkInfo(aNetworkInfo).state == NetworkInfo.State.CONNECTED) {
                            return true
                        }
                    }
                }
            return false
        }
    private val actionLog: String
        get() {
            val time = formatter.format(Date())
            return Constants.PARH_LOG + "bugLog" + time + ".txt"
        }


    /**
     * 保存文字到剪贴板
     *
     * @param context
     * @param text
     */
    fun copyToClipBoard(context: Context, text: String) {
        val clipData = ClipData.newPlainText("url", text)
        val manager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        manager.primaryClip = clipData
        Toast.makeText(context, "已复制到剪贴板", Toast.LENGTH_SHORT).show()
    }

    /**
     * 保存图片到本地
     *
     */
    fun saveBitmapToFile(view: View, url: String, bitmap: Bitmap, isShare: Boolean): Uri {
        val fileName = url.substring(url.lastIndexOf("/") + 1, url.length)
        val fileDir = File(Constants.PATH_IAMGE)
        if (!fileDir.exists()) {
            fileDir.mkdirs()
        }
        val imageFile = File(fileDir, fileName)
        val uri = Uri.fromFile(imageFile)
        if (isShare && imageFile.exists()) {
            return uri
        }
        try {
            val fos = FileOutputStream(imageFile)
            val isCompress = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            if (isCompress) {
                view.post { App.instance.toastMessage("已保存图片至\n" + uri.path) }
            } else {
                view.post { App.instance.toastMessage("保存失败") }

            }
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
            App.instance.toastMessage("保存失败")
        }

        try {
            MediaStore.Images.Media.insertImage(view.context.contentResolver, imageFile.absolutePath, fileName, null)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        view.context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        return uri
    }


    fun saveImageToFile(url: String, subscriber: ResourceSubscriber<Uri>?) {
        val flowable = Flowable.just(url).flatMap(Function<String, Publisher<Uri>> {
            val fileName = it.substring(it.lastIndexOf("/") + 1, it.length)
            val fileDir = File(Constants.PATH_IAMGE)
            if (!fileDir.exists()) {
                fileDir.mkdirs()
            }
            val imageFile = File(fileDir, fileName)
            val uri = Uri.fromFile(imageFile)
            if (imageFile.exists()) {
                return@Function Flowable.just(uri)
            }
            var inputChannel: FileChannel? = null
            var outputChannel: FileChannel? = null

            try {
                inputChannel = FileInputStream(GlideApp.with(App.instance)
                        .downloadOnly()
                        .load(it)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .submit()
                        .get()).channel
                outputChannel = FileOutputStream(imageFile).channel
                outputChannel!!.transferFrom(inputChannel, 0, inputChannel!!.size())
            } finally {
                    inputChannel?.close()
                    outputChannel?.close()

            }
            MediaStore.Images.Media.insertImage(App.instance.contentResolver, imageFile.absolutePath, fileName, null)
            App.instance.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            Flowable.just(uri)
        }).compose(RxUtil.rxSchedulerHelper())
        if (subscriber != null) {
            flowable.subscribe(subscriber)
        }
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    fun getProcessName(pid: Int): String? {
        var reader: BufferedReader? = null
        try {
            reader = BufferedReader(FileReader("/proc/$pid/cmdline"))
            var processName = reader.readLine()
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim { it <= ' ' }
            }
            return processName
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }

        }
        return null
    }

    /**
     * 屏幕截图
     *
     */
    fun takeScreenShot(activity: Activity): Bitmap {
        // View是你需要截图的View
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        val bitmap = view.drawingCache
        val b = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height)
        view.destroyDrawingCache()
        return b
    }

    fun getDateFormat(date: String): String? {
        var utilDate: Date? = null
        val df = SimpleDateFormat("yyyyMMdd",Locale.CHINA)
        try {
            utilDate = df.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            return null
        }

        val format = SimpleDateFormat("M月d日 EEEE", Locale.CHINA)
        return format.format(utilDate)
    }

    fun writeToLocaleFile(sb: String) {
        // 存储日志
        try {
            val dir = File(Constants.PARH_LOG)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val fos = File(actionLog)
            val buffer = BufferedWriter(
                    FileWriter(fos, true))
            buffer.write(sb)
            buffer.write("\r\n")
            buffer.newLine()
            buffer.flush()
            buffer.close()
        } catch (e: Exception) {
        }

    }
}