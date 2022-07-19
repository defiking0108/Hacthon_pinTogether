package org.topnetwork.pintogether.ui.activity.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.activity_create_nft.*
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.base.app.ToolbarBaseActivity
import org.topnetwork.pintogether.databinding.ActivityCreateNftBinding
import org.topnetwork.pintogether.permission.PermissionChecker
import org.topnetwork.pintogether.ui.activity.vm.CreateNftActivityVm
import org.topnetwork.pintogether.utils.LogUtils
import org.topnetwork.pintogether.utils.zxingV2.util.UriUtils
import java.io.File
import top.zibin.luban.OnCompressListener

import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.CompoundButton
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.amap.api.maps.AMap
import com.amap.api.maps.model.LatLng
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.topnetwork.pintogether.dialog.CommonDialog
import org.topnetwork.pintogether.event.SignInLocationEvent
import org.topnetwork.pintogether.map.Location
import org.topnetwork.pintogether.startCreateSuccessActivity
import org.topnetwork.pintogether.startSignInLocationActivity
import org.topnetwork.pintogether.utils.StringUtils
import org.topnetwork.pintogether.utils.ToastUtils
import org.topnetwork.pintogether.utils.luban.getPath
import org.topnetwork.pintogether.utils.nftstorage.uploadUrl

import top.zibin.luban.Luban
class CreateNftActivity : ToolbarBaseActivity<ActivityCreateNftBinding, CreateNftActivityVm>() {

    var picPath:String ?= null
    var latLng:LatLng ?= null
    var address:String = ""
    var ranges :String = "100"

    var maxNum = Int.MAX_VALUE
    var currentStep = 0
    override val layoutResId: Int
        get() = R.layout.activity_create_nft

    override fun createViewModel(): CreateNftActivityVm = ViewModelProvider(this).get(
        CreateNftActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        setToolbarTitle("创建")
        latLng = Location.currentLatlng
        viewModel?.create?.observe(this, {
            dismissLoading()
            if(!StringUtils.isEmpty(it)){
                startCreateSuccessActivity(it)
                finish()
            }
        })

        getWindow().setSoftInputMode(WindowManager.LayoutParams. SOFT_INPUT_ADJUST_PAN)
        cvb?.swatchButtonLimited?.setOnCheckedChangeListener(object :CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked){
                    cvb?.lineNumber?.visibility = View.VISIBLE
                    cvb?.llNumber?.visibility = View.VISIBLE
                }else{
                    cvb?.lineNumber?.visibility = View.GONE
                    cvb?.llNumber?.visibility = View.GONE
                }
            }
        })

        viewModel?.cid?.observe(this, Observer {
            var name = cvb?.etName?.text.toString()
            var describe = cvb?.etDescribe?.text.toString()
            latLng?.run {
                //是否限量
                var islimited = cvb?.swatchButtonLimited?.isChecked
                var num: String
                num = if(islimited!!){
                    cvb?.etNumber?.text.toString()
                }else{
                    maxNum.toString()
                }
                //是否扫码签到
                var isCodeSignIn = cvb?.swatchButtonSignIn?.isChecked
                viewModel?.create(address,it,name,describe,num,ranges,isCodeSignIn!!,latLng?.latitude.toString(),latLng?.longitude.toString(),islimited)
            }
        })

        cvb?.run {
            flAddPic.setOnClickListener {
                if (PermissionChecker.hasPermissions(this@CreateNftActivity, *PermissionChecker.CAMERA_PERMISSIONS)) {
                    jumpV2()
                } else {
                    PermissionChecker.requestPermissions(
                        this@CreateNftActivity,
                        PermissionChecker.REQUEST_CODE_PERMISSION_CAMERA,
                        *PermissionChecker.CAMERA_PERMISSIONS
                    )
                }
            }
            etDescribe.addTextChangedListener(object : TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                }

                @SuppressLint("SetTextI18n")
                override fun afterTextChanged(s: Editable?) {
                     s?.run {
                         var content = s.toString()
                         if(content.length >= 1000){
                             tvDescribeNum.text = "1000/1000"
                         }else{
                             tvDescribeNum.text = content.length.toString() + "/1000"
                         }
                     }
                }

            })

            llSignInLocation.setOnClickListener {
                latLng?.run {
                    startSignInLocationActivity(latitude,longitude,false,null)
                }

            }

            tvStep.setOnClickListener {
                if(currentStep == 0){
                    if(picPath == null){
                        ToastUtils.showLong("请上传图片")
                        return@setOnClickListener
                    }
//                    if(picCid == null){
//                        ToastUtils.showLong("图片上传失败")
//                        return@setOnClickListener
//                    }
                    if(StringUtils.isEmpty(etName.text.toString())){
                        ToastUtils.showLong("请输入名称")
                        return@setOnClickListener
                    }
                    if(StringUtils.isEmpty(etDescribe.text.toString())){
                        ToastUtils.showLong("请输入描述")
                        return@setOnClickListener
                    }

                    var islimited = cvb?.swatchButtonLimited?.isChecked!!
                    if(islimited){
                        if(StringUtils.isEmpty(etNumber.text.toString())){
                            ToastUtils.showLong("请输数量")
                            return@setOnClickListener
                        }
                    }
                    currentStep = 1
                    showUiByCurrentStep()
                }else{
                    if(StringUtils.isEmpty(address)){
                        ToastUtils.showLong("请选择地址")
                        return@setOnClickListener
                    }

                    CommonDialog.getInstance()
                        .setTitle("创建确认")
                        .setConfirm("确认")
                        .setCancel("取消")
                        .setCanCancel(false)
                        .setConfirmClickListener {
                            picPath?.run {
                                showCancelLoading("")
                                luban(Uri.fromFile(File(this)))
                            }
                        }
                        .show(supportFragmentManager)

                }
            }
        }
    }


    override fun onBackPressed() {
        if(currentStep == 1){
            currentStep = 0
            address = ""
            showUiByCurrentStep()
            return
        }
        super.onBackPressed()
    }

    //<editor-fold desc="页面跳转 新的写法">
    private val startActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            it.data?.run {
                val path = UriUtils.getImagePath(this@CreateNftActivity, it.data)
                picPath = path
                LogUtils.eTag("TheAuthentication", path)
                path?.run {
                    iv_add_pic.setImageURI(Uri.fromFile(File(this)))
                }

            }
        }

    fun jumpV2() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        startActivity.launch(intent)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        super.onPermissionsGranted(requestCode, perms)
        if (requestCode == PermissionChecker.REQUEST_CODE_PERMISSION_CAMERA) {
            jumpV2()
        }
    }

    private fun luban(uri: Uri){
        val photos:List<Uri> = arrayListOf(uri)
        Luban.with(this)
            .load<Any>(photos)
            .ignoreBy(100)
            .setTargetDir(getPath())
            .filter { path -> !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif")) }
            .setCompressListener(object : OnCompressListener {
                override fun onStart() {
                    LogUtils.eTag("CreateNftActivity","luban onStart")
                }

                override fun onSuccess(file: File) {
                    LogUtils.eTag("CreateNftActivity","luban onSuccess " + file.absolutePath)
                    picPath = file.absolutePath
                    viewModel?.uploadFile(file.path,uploadUrl)
                }

                override fun onError(e: Throwable) {
                    LogUtils.eTag("CreateNftActivity","luban onError " + e.message)
                    dismissLoading()
                }
            }).launch()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun showUiByCurrentStep(){
        if(currentStep == 0){
            cvb?.run {
                llFirstStep.visibility = View.VISIBLE
                llSecondStep.visibility = View.GONE
                rlLimited.visibility = View.VISIBLE
                llName.visibility = View.VISIBLE
                llDescribe.visibility = View.VISIBLE
                rlSecondStep.background = getDrawable(R.drawable.bg_shape_oval_999999)
                tvSecondTitle.setTextColor(Color.parseColor("#999999"))
                tvStep.setText("下一步")

            }
        }else{
            cvb?.run {
                llSecondStep.visibility = View.VISIBLE
                llFirstStep.visibility = View.GONE
                rlLimited.visibility = View.GONE
                llName.visibility = View.GONE
                llDescribe.visibility = View.GONE
                lineNumber?.visibility = View.GONE
                llNumber?.visibility = View.GONE
                rlSecondStep.background = getDrawable(R.drawable.bg_shape_oval_3665fd)
                tvSecondTitle.setTextColor(Color.parseColor("#3665FD"))
                tvStep.setText("立即创建")
                tvLocation.setText("请选择")

            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: SignInLocationEvent) {
        latLng = event.locationSearch.latLng
        address = event.locationSearch.address
        ranges = event.ranges
        LogUtils.eTag(
            "CreateNftActivity",
            "ranges: " + ranges
        )

        LogUtils.eTag(
            "CreateNftActivity",
            "latLng: " + event.locationSearch.latLng.latitude + "  " + event.locationSearch.latLng.longitude
        )

        address.run {
            cvb?.tvLocation?.setText(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}