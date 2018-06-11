package com.pudding.tangentninety.module.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey


/**
 * Created by Error on 2017/7/3 0003.
 */
@Entity
open class CollectionStoryBean{
    @PrimaryKey
    var id: Int = 0
    var title: String? = null
    var image: String? = null

    @Ignore
    var isChecked: Boolean = false
    var time: Long = 0
}
