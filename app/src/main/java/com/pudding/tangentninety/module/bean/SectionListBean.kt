package com.pudding.tangentninety.module.bean

/**
 * Created by Error on 2017/6/26 0026.
 */

open class SectionListBean {


    var data: List<DataBean>? = null

    class DataBean {
        /**
         * description : 看别人的经历，理解自己的生活
         * id : 1
         * name : 深夜惊奇
         * thumbnail : http://pic3.zhimg.com/91125c9aebcab1c84f58ce4f8779551e.jpg
         */

        var description: String? = null
        var id: Int = 0
        var name: String? = null
        var thumbnail: String? = null
    }
}
