package com.pudding.tangentninety.base

/**
 * Created by Error on 2017/6/22 0022.
 */

interface BaseView {

    fun showErrorMsg(msg: String)

    fun stateError()

    fun stateEmpty()

    fun stateLoading()

    fun stateMain()
}
