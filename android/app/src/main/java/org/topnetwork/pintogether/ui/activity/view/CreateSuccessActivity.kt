package org.topnetwork.pintogether.ui.activity.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.base.app.ToolbarBaseActivity
import org.topnetwork.pintogether.databinding.ActivityCreateSuccessBinding
import org.topnetwork.pintogether.key_gift_id
import org.topnetwork.pintogether.startNftDetails
import org.topnetwork.pintogether.ui.activity.vm.CreateSuccessActivityVm

/**
 *  作者    lgc
 *  时间    2022/7/14 16:32
 *  文件    PinTogether
 *  描述
 */
class CreateSuccessActivity:
    ToolbarBaseActivity<ActivityCreateSuccessBinding, CreateSuccessActivityVm>() {
    var id = ""
    override val layoutResId: Int
        get() = R.layout.activity_create_success

    override fun createViewModel(): CreateSuccessActivityVm = ViewModelProvider(this).get(
        CreateSuccessActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        setToolbarTitle("创建")
        intent.extras?.run {
            if(containsKey(key_gift_id)){
                id = intent.getStringExtra(key_gift_id).toString()
            }
        }

        cvb?.run {
            tvClose.setOnClickListener {
                finish()
            }
            tvDetail.setOnClickListener {
                finish()
                startNftDetails(id,0,null)
            }
        }
    }
}