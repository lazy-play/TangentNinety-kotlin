package com.pudding.tangentninety.module.event

/**
 * Created by Error on 2017/7/26 0026.
 */

class DownloadNextEvent {
    var present: Int = 0
        internal set
    var isFinish: Boolean = false
        internal set

    constructor(now: Int, total: Int) {
        present = now * 100 / total
    }

    constructor(isSuccess: Boolean) {
        isFinish = true
        present = if (isSuccess) 100 else 0
    }
}
