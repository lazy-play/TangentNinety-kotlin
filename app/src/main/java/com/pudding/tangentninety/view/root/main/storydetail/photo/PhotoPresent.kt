package com.pudding.tangentninety.view.root.main.storydetail.photo


import android.net.Uri

import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.http.CommonSubscriber
import com.pudding.tangentninety.utils.SystemUtil

import javax.inject.Inject

/**
 * Created by Error on 2017/7/3 0003.
 */

class PhotoPresent @Inject
constructor() : RxPresenter<PhotoContract.View>(), PhotoContract.Presenter {

    override fun downloadImage(url: String) {
        SystemUtil.saveImageToFile(url, object : CommonSubscriber<Uri>(mView) {
            override fun onNext(uri: Uri) {
                    mView?.showResult("已保存图片至\n" + uri.path)
            }

            override fun onError(e: Throwable) {
                    mView?.showResult("保存失败")
            }
        })
    }
}
