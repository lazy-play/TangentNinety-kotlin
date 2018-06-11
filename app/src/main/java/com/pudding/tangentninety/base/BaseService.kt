package com.pudding.tangentninety.base


import android.app.Service
import android.content.Intent
import android.os.IBinder

import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.di.component.DaggerServiceComponent
import com.pudding.tangentninety.di.component.ServiceComponent
import com.pudding.tangentninety.di.module.ServiceModule

import javax.inject.Inject

/**
 * Created by Error on 2017/7/26 0026.
 */

abstract class BaseService<in V:BaseView,P : BasePresenter<V>> : Service(), BaseView {
    @Inject
    lateinit var mPresenter: P
    protected val serviceComponent: ServiceComponent
        get() = DaggerServiceComponent.builder()
                .appComponent(App.appComponent)
                .serviceModule(serviceModule)
                .build()
    protected val serviceModule: ServiceModule
        get() = ServiceModule(this)

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        initInject()
        mPresenter.attachView(this as V)
        super.onCreate()
        initEventAndData()
    }

    override fun onDestroy() {
            mPresenter.detachView()
        super.onDestroy()
    }

    protected abstract fun initEventAndData()
    protected abstract fun initInject()

    override fun showErrorMsg(msg: String) {}


    override fun stateError() {

    }

    override fun stateEmpty() {

    }

    override fun stateLoading() {

    }

    override fun stateMain() {

    }
}
