package com.pudding.tangentninety.module.http

import android.text.TextUtils

import com.pudding.tangentninety.base.BaseView
import com.pudding.tangentninety.module.http.excepiton.ApiException
import com.pudding.tangentninety.utils.LogUtil

import io.reactivex.subscribers.ResourceSubscriber
import retrofit2.HttpException

/**
 * Created by Error on 2017/6/28 0028.
 */

abstract class CommonSubscriber<T> : ResourceSubscriber<T> {
    private var mView: BaseView? = null
    private var mErrorMsg: String? = null
    private var isShowErrorState = true

    protected constructor(view: BaseView?) {
        this.mView = view
    }

    protected constructor(view: BaseView, errorMsg: String) {
        this.mView = view
        this.mErrorMsg = errorMsg
    }

    protected constructor(view: BaseView, isShowErrorState: Boolean) {
        this.mView = view
        this.isShowErrorState = isShowErrorState
    }

    protected constructor(view: BaseView, errorMsg: String, isShowErrorState: Boolean) {
        this.mView = view
        this.mErrorMsg = errorMsg
        this.isShowErrorState = isShowErrorState
    }

    override fun onComplete() {

    }

    override fun onError(e: Throwable) {
        if (mView == null) {
            return
        }
        if (mErrorMsg != null && !TextUtils.isEmpty(mErrorMsg)) {
            mView!!.showErrorMsg(mErrorMsg!!)
        } else if (e is ApiException) {
            mView!!.showErrorMsg(e.toString())
        } else if (e is HttpException) {
            mView!!.showErrorMsg("数据加载失败")
        } else {
            mView!!.showErrorMsg("未知错误")
            LogUtil.d(e.toString())
        }
        if (isShowErrorState) {
            mView!!.stateError()
        }
    }
}

