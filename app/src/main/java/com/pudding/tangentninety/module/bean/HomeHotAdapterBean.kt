package com.pudding.tangentninety.module.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Error on 2017/7/3 0003.
 */

class HomeHotAdapterBean : Parcelable {
    var storyInfoBean: StoryInfoBean? = null

    var title: String? = null

    var type: Int = 0
        private set

    constructor(storyInfoBean: StoryInfoBean) {
        this.storyInfoBean = storyInfoBean
        type = TYPE_BEAN
    }

    constructor(title: String) {
        this.title = title
        type = TYPE_TITLE
    }

    constructor(source: Parcel) {
        storyInfoBean = source.readParcelable(StoryInfoBean::class.java.classLoader)
        title = source.readString()
        type = source.readInt()
    }
    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeParcelable(storyInfoBean,0)
        writeString(title)
        writeInt(type)
    }

    companion object {
        const val TYPE_BEAN = 1

        const val TYPE_TITLE = 2

        @JvmField
        val CREATOR: Parcelable.Creator<HomeHotAdapterBean> = object : Parcelable.Creator<HomeHotAdapterBean> {
            override fun createFromParcel(source: Parcel): HomeHotAdapterBean = HomeHotAdapterBean(source)
            override fun newArray(size: Int): Array<HomeHotAdapterBean?> = arrayOfNulls(size)
        }
    }
}
