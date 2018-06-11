package com.pudding.tangentninety.app

import android.os.Environment

import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory

import java.io.File

/**
 * Created by Error on 2017/6/22 0022.
 */

object Constants {
    //下载时间区间天数
    const val DOWNLOAD_CACHE_TIME = 2
    //缓存时间
    const val NET_CACHE_TIME = 0
    const val NO_NET_CACHE_TIME = 60 * 60 * 24 * 28

    //缓存目录
    val PATH_DATA = App.instance.cacheDir.absolutePath + File.separator
    val PATH_NET_CACHE = PATH_DATA + "data" + File.separator + "NetCache"
    val PATH_GLIDE_CACHE = PATH_DATA + InternalCacheDiskCacheFactory.DEFAULT_DISK_CACHE_DIR

    //图片存储目录
    val PATH_SDCARD = Environment.getExternalStorageDirectory().absolutePath + File.separator + "Pudding" + File.separator + "TangentNinety" + File.separator
    val PATH_IAMGE = PATH_SDCARD + "image" + File.separator
    val PARH_LOG = PATH_SDCARD + "log" + File.separator

    //SharePreferences的key
    const val SP_NIGHT_MODE = "night_mode"
    const val SP_NO_IMAGE = "no_image"
    const val SP_AUTO_CACHE = "auto_cache"
    const val SP_BIG_FONT = "big_font"

}
