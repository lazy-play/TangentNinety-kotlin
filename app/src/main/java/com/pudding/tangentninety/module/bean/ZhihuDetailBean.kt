package com.pudding.tangentninety.module.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Error on 2017/6/30 0030.
 */
@Entity
open class ZhihuDetailBean {

    var body: String? = null
    var image_source: String? = null
    var title: String? = null
    var image: String? = null
    var share_url: String? = null
    var ga_prefix: String? = null
    var type: Int = 0
    @PrimaryKey
    var id: Int = 0
    var time: Long = 0
}
