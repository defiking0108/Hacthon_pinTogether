package org.topnetwork.pintogether.ui.activity.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.amap.api.maps.AMapUtils
import com.amap.api.maps.model.LatLng
import com.topnetwork.zxing.IScanResultCallBack
import com.topnetwork.zxingV2.ScanManager
import com.topnetwork.zxingV2.ScanManager.registerScanResultCallBack
import com.topnetwork.zxingV2.ScanManager.unRegisterScanResultCallBack
import com.topnetwork.zxingV2.startCaptureActivity
import org.topnetwork.pintogether.*
import org.topnetwork.pintogether.base.app.ToolbarBaseActivity
import org.topnetwork.pintogether.databinding.ActivityToReceiveBinding
import org.topnetwork.pintogether.extension.loadImage
import org.topnetwork.pintogether.map.Location
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.permission.PermissionChecker
import org.topnetwork.pintogether.permission.PermissionChecker.REQUEST_CODE_PERMISSION_CAMERA
import org.topnetwork.pintogether.ui.activity.vm.ToReceiveActivityVm
import org.topnetwork.pintogether.utils.ActivityUtils
import org.topnetwork.pintogether.utils.ToastUtils
import java.math.BigDecimal

class ToReceiveActivity : ToolbarBaseActivity<ActivityToReceiveBinding, ToReceiveActivityVm>() {
    private val scanKey = MainActivity::class.java.simpleName
    var nearBy:NearBy ?= null
    override val layoutResId: Int
        get() = R.layout.activity_to_receive

    override fun createViewModel(): ToReceiveActivityVm = ViewModelProvider(this).get(
        ToReceiveActivityVm::class.java
    )

    @SuppressLint("SetTextI18n")
    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        cvb?.tvToReceive?.isClickable = false

        if(intent.extras?.containsKey(key_gift_id)!!){
            //通过id获取详情
            viewModel?.getDetails(intent.extras?.getString(key_gift_id)!!)
            viewModel?.checkRequest(intent.extras?.getString(key_gift_id)!!)
            viewModel?.details?.observe(this, Observer {
                nearBy = it
                nearBy?.run {
                    cvb?.ivPic?.loadImage(cid,R.drawable.ic_get_nft_holder)
                    cvb?.tvName?.setText(name)
                    if(isSign){
                        cvb?.llCode?.visibility = View.VISIBLE
                        cvb?.lineCode?.visibility = View.VISIBLE
                    }
                    cvb?.tvHint?.setText(description)
                    cvb?.tvLocation?.setText(address)
//                    var remainingNum = BigDecimal(num).subtract(BigDecimal(total))
                    //剩余可领：100
                    if(isNumLimit){
                        cvb?.tvRemaining?.setText("剩余可领：$num")
                    }
                    else{
                        cvb?.tvRemaining?.setText("剩余可领：∞")
                    }
                    cvb?.tvValidation?.setOnClickListener {
                        showCancelLoading("")
                        cvb?.tvValidation?.postDelayed({
                            dismissLoading()
                            var validationLocation = validationLocation(this)
                            if(validationLocation){
                                cvb?.ivValidationThrough?.visibility = View.VISIBLE
                                cvb?.tvValidation?.visibility = View.GONE
                            }else{
                                cvb?.ivValidationThrough?.visibility = View.GONE
                                cvb?.tvValidation?.visibility = View.VISIBLE
                            }
                            isToReceive()
                        },1000)

                    }
                    signInVisible(true)
                }
            })
        }else{
            nearBy = intent.extras?.getParcelable(key_near_by)
            nearBy?.run {
                viewModel?.checkRequest(giftId)
                cvb?.ivPic?.loadImage(cid,R.drawable.ic_get_nft_holder)
                if(isSign){
                    cvb?.llCode?.visibility = View.VISIBLE
                    cvb?.lineCode?.visibility = View.VISIBLE
                }
                cvb?.tvName?.setText(name)
                cvb?.tvHint?.setText(description)
                cvb?.tvLocation?.setText(address)
                //剩余可领：100
                if(isNumLimit){
                    cvb?.tvRemaining?.setText("剩余可领：$num")
                }
                else{
                    cvb?.tvRemaining?.setText("剩余可领：∞")
                }
                cvb?.tvValidation?.setOnClickListener {
                    showCancelLoading("")
                    cvb?.tvValidation?.postDelayed({
                        dismissLoading()
                        var validationLocation = validationLocation(this)
                        if(validationLocation){
                            cvb?.ivValidationThrough?.visibility = View.VISIBLE
                            cvb?.tvValidation?.visibility = View.GONE
                        }else{
                            cvb?.ivValidationThrough?.visibility = View.GONE
                            cvb?.tvValidation?.visibility = View.VISIBLE
                        }
                        isToReceive()
                    },1000)
                }
            }
        }

        cvb?.tvSignIn?.setOnClickListener {
            if (PermissionChecker.hasPermissions(this@ToReceiveActivity, *PermissionChecker.CAMERA_PERMISSIONS)) {
                startCaptureActivity(scanKey,100)
            } else {
                PermissionChecker.requestPermissions(
                    this@ToReceiveActivity,
                    REQUEST_CODE_PERMISSION_CAMERA,
                    *PermissionChecker.CAMERA_PERMISSIONS
                )
            }
            isToReceive()

        }

        cvb?.llMap?.setOnClickListener {
            nearBy?.run {
                Location.currentLatlng?.run {
                    startToReceiveLocationActivity(lat,lng,latitude,longitude,cid,ranges.toDouble())
                }

            }

        }

        registerScanResultCallBack(scanKey,object :IScanResultCallBack{
            override fun result(result: String?) {
                result?.run {
                    if(result.isNotEmpty()){
                        nearBy?.run {
                            if(giftId.equals(result)){
                                showCancelLoading("")
                                viewModel?.validationSignIn(result)
                            }else{
                                ToastUtils.showLong("不匹配哦")
                            }
                        }

                    }else{
                        ToastUtils.showLong("签到失败")
                    }

                    ActivityUtils.pop()
                }
            }

        })

        viewModel?.check?.observe(this, Observer {
            check = it
            if(!check){
                cvb?.tvToReceive?.text = "已领完"
            }
            isToReceive()
        })

        viewModel?.validation?.observe(this, Observer {
            dismissLoading()
            signInVisible(it)
            isToReceive()
        })

        viewModel?.callback?.observe(this, Observer {
            if(it){
                startToReceiveSuccessActivity()
                finish()
            }
            dismissLoading()
        })
    }

    /**
     *
     * 验证地址
     */
     fun validationLocation(nearBy:NearBy?):Boolean{
        if(Location.currentLatlng == null){
            ToastUtils.showLong("暂时无法获取您的位置，请打开位置获取权限")
            return false
        }
        nearBy?.run {
            var distance1:Double =
                AMapUtils.calculateLineDistance(Location.currentLatlng, LatLng(lat,lng)).toDouble()
            if(BigDecimal(ranges).subtract(BigDecimal.valueOf(distance1)) >= BigDecimal.ZERO){
                return true
            }else{
                ToastUtils.showLong("您不在领取范围内")
                return false
            }
        }

        return false
     }


    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        super.onPermissionsGranted(requestCode, perms)
        if(requestCode == REQUEST_CODE_PERMISSION_CAMERA){
            startCaptureActivity(scanKey,100)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegisterScanResultCallBack(scanKey)
    }

    fun signInVisible(isVisible:Boolean){
        if(isVisible){
            //签名验证成功
            cvb?.ivSignInThrough?.visibility = View.VISIBLE
            cvb?.tvSignIn?.visibility = View.GONE
        }else{
            cvb?.ivSignInThrough?.visibility = View.VISIBLE
            cvb?.tvSignIn?.visibility = View.GONE
        }
    }

    var check = false

    //是否可领取
    fun isToReceive(){
        nearBy?.run {
            var remainingNum = BigDecimal(num).subtract(BigDecimal(total))
            cvb?.run {
                if(isSign){
                    if(ivSignInThrough.visibility == View.VISIBLE && ivValidationThrough.visibility == View.VISIBLE &&
                        remainingNum.compareTo(BigDecimal.ZERO) > -1 && check){
                        tvToReceive.setBackgroundResource(R.drawable.bg_shape_r8_3665fd)
                        tvToReceive.isClickable = true

                        tvToReceive.setOnClickListener {
                            showCancelLoading("")
                            viewModel?.toReceive(giftId)
                        }
                    }
                    else{
                        tvToReceive.setBackgroundResource(R.drawable.bg_shape_r8_bbbbbb)
                        tvToReceive.isClickable = false
                    }
                }else{
                    if(ivValidationThrough.visibility == View.VISIBLE &&
                        remainingNum.compareTo(BigDecimal.ZERO) > -1 && check){
                        tvToReceive.setBackgroundResource(R.drawable.bg_shape_r8_3665fd)
                        tvToReceive.isClickable = true

                        tvToReceive.setOnClickListener {
                            showCancelLoading("")
                            viewModel?.toReceive(giftId)
                        }
                    }
                    else{
                        tvToReceive.setBackgroundResource(R.drawable.bg_shape_r8_bbbbbb)
                        tvToReceive.isClickable = false
                    }
                }

            }
        }

    }


}