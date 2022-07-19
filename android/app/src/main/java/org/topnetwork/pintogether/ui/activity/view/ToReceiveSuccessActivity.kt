package org.topnetwork.pintogether.ui.activity.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.base.app.ToolbarBaseActivity
import org.topnetwork.pintogether.databinding.ActivityToReceiveSuccessBinding
import org.topnetwork.pintogether.ui.activity.vm.ToReceiveSuccessActivityVm

/**
 *  作者    lgc
 *  时间    2022/7/14 14:57
 *  文件    PinTogether
 *  描述
 */
class ToReceiveSuccessActivity:
    ToolbarBaseActivity<ActivityToReceiveSuccessBinding, ToReceiveSuccessActivityVm>() {
    override val layoutResId: Int
        get() = R.layout.activity_to_receive_success

    override fun createViewModel(): ToReceiveSuccessActivityVm = ViewModelProvider(this).get(
        ToReceiveSuccessActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        cvb?.tvClose?.setOnClickListener {
            finish()
        }
    }
}