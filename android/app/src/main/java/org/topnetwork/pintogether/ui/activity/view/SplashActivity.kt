package org.topnetwork.pintogether.ui.activity.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import org.topnetwork.pintogether.*
import org.topnetwork.pintogether.base.app.BaseActivity
import org.topnetwork.pintogether.databinding.ActivitySplashBinding
import org.topnetwork.pintogether.ui.activity.vm.SplashActivityVm
import org.topnetwork.pintogether.utils.PrefsUtil

class SplashActivity: BaseActivity<ActivitySplashBinding, SplashActivityVm>() {
    override val layoutResId: Int
        get() = R.layout.activity_splash

    override fun createViewModel(): SplashActivityVm = ViewModelProvider(this).get(
        SplashActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        val isLogin : Boolean = PrefsUtil.get(sp_is_login,false) as Boolean
        AppData.isLogin = isLogin
        if(isLogin){
            AppData.address = PrefsUtil.get(sp_address,"") as String
            startMain()
        }else{
            startLogin()
        }
    }
}