package org.topnetwork.pintogether.ui.activity.view

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.ViewModelProvider
import io.reactivex.Single
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.base.app.ToolbarBaseActivity
import org.topnetwork.pintogether.databinding.ActivityMeCodeBinding
import org.topnetwork.pintogether.key_gift_id
import org.topnetwork.pintogether.permission.PermissionChecker
import org.topnetwork.pintogether.ui.activity.vm.MeCodeActivityVm
import org.topnetwork.pintogether.utils.*
import java.io.IOException

/**
 *  作者    lgc
 *  时间    2022/7/14 11:57
 *  文件    PinTogether
 *  描述
 */
class MeCodeActivity : ToolbarBaseActivity<ActivityMeCodeBinding, MeCodeActivityVm>() {
    var savePath =
        Environment.getExternalStorageDirectory().absolutePath + "/Download/NQrCode" + System.currentTimeMillis() + ".png"
    var mQrBitmap: Bitmap?= null

    var giftId:String ?= null
    override val layoutResId: Int
        get() = R.layout.activity_me_code

    override fun createViewModel(): MeCodeActivityVm = ViewModelProvider(this).get(
        MeCodeActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        setToolbarTitle("二维码")

        intent.extras?.run {
            if(containsKey(key_gift_id)){
                giftId = intent.getStringExtra(key_gift_id).toString()
            }
        }

        giftId?.run {
            createQrCode(this)
        }
        cvb?.save?.setOnClickListener {
            loadQrcode()
        }

    }

    /**
     * 生成二维码
     */
    @SuppressLint("CheckResult")
    fun createQrCode(giftId:String) {
        Single.fromCallable {
            QRUtils.Create2DCode(giftId, SizeUtils.dp2px(270f),SizeUtils.dp2px(270f))
        }
            .subscribe { bitmap ->
                mQrBitmap = bitmap
                cvb?.ivPic?.setImageBitmap(mQrBitmap)

            }
    }

    /**
     * 下载二维码
     *
     */
    private fun loadQrcode() {
        savePath =
            Environment.getExternalStorageDirectory().absolutePath + "/Download/NQrCode" + System.currentTimeMillis() + ".png"
        //判断是否有权限
        if (PermissionChecker.hasPermissions(
                this, Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            realSavePic()
        } else {
            PermissionChecker.requestPermissions(
                this,
                PermissionChecker.REQUEST_CODE_PERMISSION_SD,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * 有权限直接保存
     */
    private fun realSavePic() {
        LogUtils.eTag("SharePicUtils", "realSavePic")
        mQrBitmap?.run {
            try {
                Single.fromCallable {
                    SharePicUtils.saveImageToGallery(
                        this@MeCodeActivity,
                        savePath,
                        this
                    )
                }
                    .subscribe { _ ->
                        dismissLoading()
                        ToastUtils.showLong("保存成功")
                    }

            } catch (e: IOException) {
                e.printStackTrace()
                dismissLoading()
                ToastUtils.showLong("保存失败")
            }
        }


    }
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        super.onPermissionsGranted(requestCode, perms)
        if (requestCode == PermissionChecker.REQUEST_CODE_PERMISSION_SD) {
            realSavePic()
        }
    }



}