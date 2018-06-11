package com.pudding.tangentninety.module.event

/**
 * Created by Error on 2017/8/7 0007.
 */

class AutoDownloadModeChange(isOpen: Boolean) {
    var isOpen: Boolean = false
        internal set

    init {
        this.isOpen = isOpen
    }
}
