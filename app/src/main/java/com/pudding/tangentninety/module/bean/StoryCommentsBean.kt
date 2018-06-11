package com.pudding.tangentninety.module.bean

/**
 * Created by Error on 2017/7/24 0024.
 */

open class StoryCommentsBean {

    var comments: List<CommentsBean>? = null

    class CommentsBean {
        /**
         * author : 晨曦中的拥抱
         * content : 好棒，可以去写个回答啊
         * avatar : http://pic2.zhimg.com/da7f21892456af2079b8488511937b21_im.jpg
         * time : 1500879144
         * reply_to : {"content":"看到倒写典故，我满脑子想着藕娃哪吒想变成有血有肉的人，从太乙真人手下逃出大闹东海，老龙王给烦的不行，想亲自动手灭了哪吒又怕得罪太乙，就强行甩锅给陈塘关总兵李靖。于是用法力把哪吒变成一个肉球，塞到李靖夫人的肚子里，这一消化就消化了三年又六个月\u2026\u2026","status":0,"id":29713932,"author":"爱做梦的被害妄想者"}
         * id : 29715774
         * likes : 0
         */

        var author: String? = null
        var content: String? = null
        var avatar: String? = null
        var time: Int = 0
        var reply_to: ReplyToBean? = null
        var id: Int = 0
        var likes: Int = 0

        class ReplyToBean {
            /**
             * content : 看到倒写典故，我满脑子想着藕娃哪吒想变成有血有肉的人，从太乙真人手下逃出大闹东海，老龙王给烦的不行，想亲自动手灭了哪吒又怕得罪太乙，就强行甩锅给陈塘关总兵李靖。于是用法力把哪吒变成一个肉球，塞到李靖夫人的肚子里，这一消化就消化了三年又六个月……
             * status : 0
             * id : 29713932
             * author : 爱做梦的被害妄想者
             */

            var content: String? = null
            var status: Int = 0
            var id: Int = 0
            var author: String? = null
        }
    }
}
