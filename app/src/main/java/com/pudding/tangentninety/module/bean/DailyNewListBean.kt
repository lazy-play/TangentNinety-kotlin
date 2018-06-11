package com.pudding.tangentninety.module.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Error on 2017/6/26 0026.
 */

open class DailyNewListBean(source: Parcel) : DailyListBean(source), Parcelable {
    var top_stories: List<TopStoriesBean>?

    class TopStoriesBean(source: Parcel) : Parcelable {
        var image: String? = null

        var type: Int = 0

        var id: Int = 0

        var ga_prefix: String? = null

        var title: String? = null

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(image)
            writeInt(type)
            writeInt(id)
            writeString(ga_prefix)
            writeString(title)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<TopStoriesBean> = object : Parcelable.Creator<TopStoriesBean> {
                override fun createFromParcel(source: Parcel): TopStoriesBean = TopStoriesBean(source)
                override fun newArray(size: Int): Array<TopStoriesBean?> = arrayOfNulls(size)
            }
        }

        init {
            image = source.readString()
            type = source.readInt()
            id = source.readInt()
            ga_prefix = source.readString()
            title = source.readString()
        }
    }

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        super.writeToParcel(dest, flags)
        this.writeList(top_stories)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DailyNewListBean> = object : Parcelable.Creator<DailyNewListBean> {
            override fun createFromParcel(source: Parcel): DailyNewListBean = DailyNewListBean(source)
            override fun newArray(size: Int): Array<DailyNewListBean?> = arrayOfNulls(size)
        }
    }

    init {
        top_stories=source.readArrayList(TopStoriesBean::class.java.classLoader) as List<TopStoriesBean>
    }
}
