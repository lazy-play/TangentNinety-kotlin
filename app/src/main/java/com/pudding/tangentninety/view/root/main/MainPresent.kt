package com.pudding.tangentninety.view.root.main

import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.pudding.tangentninety.base.RxPresenter
import com.pudding.tangentninety.module.DataManager
import com.pudding.tangentninety.module.http.CommonSubscriber

import javax.inject.Inject

import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * Created by Error on 2017/6/30 0030.
 */

class MainPresent @Inject
constructor(private val mDataManager: DataManager, private val locationClient: LocationClient) : RxPresenter<MainContract.View>(), MainContract.Presenter {
    override val isAutoDownload: Boolean
        get() = mDataManager.autoCacheState
    private var listener: MyLocationListener? = null

    override fun attachView(view: MainContract.View) {
        super.attachView(view)
        listener = MyLocationListener()
    }

    override fun detachView() {
        super.detachView()
        locationClient.unRegisterLocationListener(listener!!)
        listener = null
    }

    override fun getNetState() {
        locationClient.registerLocationListener(listener!!)
        locationClient.start()
    }


    internal inner class MyLocationListener : BDAbstractLocationListener() {

        override fun onReceiveLocation(bdLocation: BDLocation) {
            locationClient.requestHotSpotState()
        }

        override fun onConnectHotSpotMessage(connectWifiMac: String?, hotSpotState: Int) {
            locationClient.stop()
            Flowable.just(hotSpotState).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : CommonSubscriber<Int>(mView) {
                        override fun onNext(i: Int?) {
                                mView?.setNetState(i == 0)
                        }
                    })
        }
    }
}
