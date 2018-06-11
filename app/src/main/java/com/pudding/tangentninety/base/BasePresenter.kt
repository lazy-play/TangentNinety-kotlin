package com.pudding.tangentninety.base

/**
 * Created by Error on 2017/6/22 0022.
 */

interface BasePresenter<in T : BaseView> {

    fun attachView(view: T)

    fun detachView()
}
