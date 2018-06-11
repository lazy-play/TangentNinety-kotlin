package com.pudding.tangentninety.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Error on 2017/6/22 0022.
 */

open class RxPresenter<T : BaseView> : BasePresenter<T> {

    protected var mView: T? = null
    private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable()}

    protected fun unSubscribe() {
            mCompositeDisposable.clear()
    }

    override fun attachView(view: T) {
        this.mView = view
    }

    override fun detachView() {
        this.mView = null
        unSubscribe()
    }
    fun <T:BaseView> Disposable.addSubscribe(present:RxPresenter<T>){
        present.mCompositeDisposable.add(this)
    }
}
