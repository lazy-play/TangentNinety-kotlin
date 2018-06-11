package com.pudding.tangentninety.base

import android.os.Bundle
import android.view.View

import com.pudding.tangentninety.app.App
import com.pudding.tangentninety.di.component.DaggerFragmentComponent
import com.pudding.tangentninety.di.component.FragmentComponent
import com.pudding.tangentninety.di.module.FragmentModule

import javax.inject.Inject

/**
 * Created by Error on 2017/6/22 0022.
 */

abstract class BaseFragment<in V:BaseView,P : BasePresenter<V>> : SimpleFragment(), BaseView {

    @Inject
    lateinit var mPresenter: P

    protected val fragmentComponent: FragmentComponent
        get() = DaggerFragmentComponent.builder()
                .appComponent(App.appComponent)
                .fragmentModule(fragmentModule)
                .build()

    private val fragmentModule: FragmentModule
        get() = FragmentModule(this)

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initInject()
        mPresenter.attachView(this as V)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
            mPresenter.detachView()
        super.onDestroyView()
    }

    override fun showErrorMsg(msg: String) {}

    override fun stateError() {

    }

    override fun stateEmpty() {

    }

    override fun stateLoading() {

    }

    override fun stateMain() {

    }

    protected abstract fun initInject()
}
