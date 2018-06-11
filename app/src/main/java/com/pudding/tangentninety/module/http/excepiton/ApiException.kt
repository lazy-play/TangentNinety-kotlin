package com.pudding.tangentninety.module.http.excepiton

/**
 * Created by Error on 2017/6/22 0022.
 */

class ApiException : Exception {

    var code: Int = 0

    constructor(msg: String) : super(msg) {}

    constructor(msg: String, code: Int) : super(msg) {
        this.code = code
    }
}