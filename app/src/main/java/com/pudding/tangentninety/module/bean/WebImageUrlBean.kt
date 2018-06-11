package com.pudding.tangentninety.module.bean

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


/**
 * Created by Error on 2017/7/12 0012.
 */
@Entity
open class WebImageUrlBean(@PrimaryKey val url:String)
