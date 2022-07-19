package org.topnetwork.pintogether.base.app

import android.app.Application
import io.reactivex.plugins.RxJavaPlugins
import org.topnetwork.pintogether.base.NormalBaseConfig
import org.topnetwork.pintogether.utils.ToastUtils

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ToastUtils.init(this)
        NormalBaseConfig.init(this)
        RxJavaPlugins.setErrorHandler { }
    }
}
