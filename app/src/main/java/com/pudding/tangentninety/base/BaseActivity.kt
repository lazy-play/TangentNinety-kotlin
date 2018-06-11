package com.pudding.tangentninety.base
import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.di.component.ActivityComponent
import com.pudding.tangentninety.di.component.DaggerActivityComponent
import com.pudding.tangentninety.di.module.ActivityModule

import javax.inject.Inject

/**
 * Created by Error on 2017/6/22 0022.
 */

abstract class BaseActivity<in V:BaseView,P: BasePresenter<V>> : SimpleActivity(), BaseView {

    @Inject
    lateinit var mPresenter: P
    protected val activityComponent: ActivityComponent
        get() = DaggerActivityComponent.builder()
                .appComponent(App.appComponent)
                .activityModule(activityModule)
                .build()

    protected val activityModule: ActivityModule
        get() = ActivityModule(this)

    override fun onViewCreated() {
        super.onViewCreated()
        initInject()
        mPresenter.attachView(this as V)
    }

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }

    override fun showErrorMsg(msg: String) {}
    override fun stateError() {}
    override fun stateEmpty() {}
    override fun stateLoading() {}
    override fun stateMain() {}
    protected abstract fun initInject()
}
