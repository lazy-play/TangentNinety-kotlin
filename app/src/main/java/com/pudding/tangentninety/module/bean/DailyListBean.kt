package com.pudding.tangentninety.module.bean

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by Error on 2017/6/26 0026.
 */

open class DailyListBean(source: Parcel) : Parcelable {
    /**
     * date : 20170626
     * stories : [{"images":["https://pic3.zhimg.com/v2-c2597b3c4f43297457a032976ec41b7a.jpg"],"type":0,"id":9491311,"ga_prefix":"062614","title":"只想安静地放空表情，一不小心却向领导摆了张臭脸"},{"images":["https://pic3.zhimg.com/v2-d984c2f6dd9a325ea4a559cf07516ff2.jpg"],"type":0,"id":9492269,"ga_prefix":"062613","title":"《帝国时代》20 年：你可还记得大明湖畔的伐木工？"},{"images":["https://pic3.zhimg.com/v2-b967db3abbe9399f0f363b0b1f7c52f6.jpg"],"type":0,"id":9486441,"ga_prefix":"062612","title":"大误 · 朕这皇帝我不当了"},{"images":["https://pic4.zhimg.com/v2-15f38eb7bb3137c75bd7dc8a3264cd0b.jpg"],"type":0,"id":9493628,"ga_prefix":"062611","title":"贴吧「关停并转」，高层人事动荡，陆奇和百度寻找下一个千亿美元计划"},{"images":["https://pic2.zhimg.com/v2-8aa443402ef9d4ca7c21d3784b1fdc41.jpg"],"type":0,"id":9493089,"ga_prefix":"062610","title":"作为一个辩论新手，如何自我提高？"},{"images":["https://pic2.zhimg.com/v2-4cc947cfa7d8e47dd44b5eeea757cc29.jpg"],"type":0,"id":9492517,"ga_prefix":"062609","title":"既然吃下去都是热量，我们为什么不把吃饭改成吃糖？"},{"images":["https://pic3.zhimg.com/v2-03d63fd3c401f0671fd601af4b891bbe.jpg"],"type":0,"id":9492978,"ga_prefix":"062608","title":"员工下班就准点走，真是没有「事业心」啊"},{"images":["https://pic4.zhimg.com/v2-d6818fb887fb602034d9d1512d48b21f.jpg"],"type":0,"id":9493083,"ga_prefix":"062607","title":"这些被遗忘的编程语言，还在诉说他们的故事"},{"images":["https://pic2.zhimg.com/v2-a632e6650b2f16e8224f5aee23df3471.jpg"],"type":0,"id":9492565,"ga_prefix":"062607","title":"一人一票选 CEO 的公司能赚到钱吗？"},{"images":["https://pic1.zhimg.com/v2-a23785b2003cdfe2192bdb5afa72fcc0.jpg"],"type":0,"id":9492696,"ga_prefix":"062607","title":"老人摔倒要不要扶？冷静，先听医生怎么说"},{"images":["https://pic3.zhimg.com/v2-9ba48b44a7ba09ce5984009e8b10b62e.jpg"],"type":0,"id":9492380,"ga_prefix":"062606","title":"瞎扯 · 如何正确地吐槽"}]
     * top_stories : [{"image":"https://pic1.zhimg.com/v2-582f85f6b7deaaf9aaadb53fbf1fb3a8.jpg","type":0,"id":9493628,"ga_prefix":"062611","title":"贴吧「关停并转」，高层人事动荡，陆奇和百度寻找下一个千亿美元计划"},{"image":"https://pic1.zhimg.com/v2-18287777ea60f73f7e00de07ea3bb354.jpg","type":0,"id":9492696,"ga_prefix":"062607","title":"老人摔倒要不要扶？冷静，先听医生怎么说"},{"image":"https://pic2.zhimg.com/v2-dcaac62fda5695da379d289088bb50cd.jpg","type":0,"id":9491805,"ga_prefix":"062510","title":"传说中最美的荧光海，其实在国内也有"},{"image":"https://pic2.zhimg.com/v2-b86e151e46bc321f4abf34caef8b4445.jpg","type":0,"id":9491681,"ga_prefix":"062507","title":"他们回家了吗？他们相会了吗？只是我们永远都不知道了"},{"image":"https://pic2.zhimg.com/v2-dbb5f84d70b41b06359e6cb9cef38ab5.jpg","type":0,"id":9491009,"ga_prefix":"062509","title":"作为医生，我亲身参与过汶川、玉树和雅安地震灾后救援"}]
     */

    var date: String?

    var stories: List<StoryInfoBean>?

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(date)
        writeList(stories)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<DailyListBean> = object : Parcelable.Creator<DailyListBean> {
            override fun createFromParcel(source: Parcel): DailyListBean = DailyListBean(source)
            override fun newArray(size: Int): Array<DailyListBean?> = arrayOfNulls(size)
        }
    }

    init {
        date=source.readString()
        stories=source.readArrayList(StoryInfoBean::class.java.classLoader) as List<StoryInfoBean>
    }
}
