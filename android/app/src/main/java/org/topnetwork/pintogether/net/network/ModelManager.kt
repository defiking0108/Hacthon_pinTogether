package com.topnetwork.net.network

import android.annotation.SuppressLint
import com.topnetwork.net.network.transformer.TRANSACTION_LOADING
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.topnetwork.pintogether.net.network.transformer.TransformerManager

@SuppressLint("CheckResult")
fun <T> requestApiOnResult(observable: Observable<T>, observer: Observer<T>, isShowLoading: Boolean) {
    var observable1: Observable<T> = observable.compose(io2Main())
    if (TransformerManager.getInstance().hashMap.isNotEmpty()) {
        for ((key, value) in TransformerManager.getInstance().hashMap) {
            if (key == TRANSACTION_LOADING) {
                if (isShowLoading) {
                    observable1 = observable1.compose(value.apply())
                }
            }
        }
    }
    observable1.subscribe(observer)
}

/**
 * @author lgc
 * @desc 线程调度+绑定生命周期
 */
private fun <T> io2Main(): ObservableTransformer<T, T> {
    return ObservableTransformer { upstream ->
        upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
}
