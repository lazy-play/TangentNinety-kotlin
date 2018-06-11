package com.pudding.tangentninety.module.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Error on 2017/6/29 0029.
 */

open class StoryInfoBean(source: Parcel) : Parcelable {
    var type: Int = 0

    var ga_prefix: String? = null

    var id: Int = 0

    var title: String? = null

    var images: List<String>? = null

    var isHasRead: Boolean = false

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(type)
        writeString(ga_prefix)
        writeInt(id)
        writeString(title)
        writeStringList(images)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<StoryInfoBean> = object : Parcelable.Creator<StoryInfoBean> {
            override fun createFromParcel(source: Parcel): StoryInfoBean = StoryInfoBean(source)
            override fun newArray(size: Int): Array<StoryInfoBean?> = arrayOfNulls(size)
        }
    }

    init {
        type = source.readInt()
        ga_prefix = source.readString()
        id = source.readInt()
        title = source.readString()
        images = source.createStringArrayList()
    }
}