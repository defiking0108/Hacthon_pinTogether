package org.topnetwork.pintogether.ui.fragment.view

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.AMap.OnMapTouchListener
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.adapter.PinNftListAdapter
import org.topnetwork.pintogether.base.app.BaseFragment
import org.topnetwork.pintogether.databinding.FragmentPinBinding
import org.topnetwork.pintogether.map.Location
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.startToReceiveActivity
import org.topnetwork.pintogether.ui.fragment.vm.PinFragmentVm
import java.lang.Exception
import android.graphics.Bitmap
import android.graphics.Point

import com.bumptech.glide.request.target.SimpleTarget

import com.bumptech.glide.Glide

import android.view.LayoutInflater
import android.widget.ImageView
import com.bumptech.glide.request.transition.Transition
import android.view.animation.LinearInterpolator
import androidx.lifecycle.Observer
import com.amap.api.maps.model.*
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.ScaleAnimation
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeResult
import com.topnetwork.zxing.IScanResultCallBack
import com.topnetwork.zxingV2.ScanManager
import com.topnetwork.zxingV2.startCaptureActivity
import org.greenrobot.eventbus.EventBus
import org.topnetwork.pintogether.base.app.BaseLazyLoadFragment
import org.topnetwork.pintogether.event.LocationChangedEvent
import org.topnetwork.pintogether.permission.PermissionChecker
import org.topnetwork.pintogether.startNftDetails
import org.topnetwork.pintogether.utils.ActivityUtils
import org.topnetwork.pintogether.utils.LogUtils
import org.topnetwork.pintogether.utils.StringUtils
import org.topnetwork.pintogether.utils.ToastUtils
import org.topnetwork.pintogether.utils.image.transform.GlideCircleTransform
import kotlin.math.roundToInt

class PinFragment : BaseLazyLoadFragment<FragmentPinBinding, PinFragmentVm>(), LocationSource,
    AMapLocationListener, OnMapTouchListener, AMap.OnMarkerClickListener,
    GeocodeSearch.OnGeocodeSearchListener {

    val scanKey = PinFragment::class.java.simpleName
    private var aMap: AMap? = null
    private var mListener: OnLocationChangedListener? = null
    private var mlocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null

    var useMoveToLocationWithMapMode = true

    //自定义定位小蓝点的Marker
    var firstLocation = false

    //坐标和经纬度转换工具
    var projection: Projection? = null

    var mAdapter: PinNftListAdapter?=null

    override fun getLayoutResId(): Int = R.layout.fragment_pin

    override fun createViewModel(): PinFragmentVm =  ViewModelProvider(this).get(
        PinFragmentVm::class.java
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MapsInitializer.updatePrivacyShow(context, true, true)
        MapsInitializer.updatePrivacyAgree(context, true)
        cvb?.map?.onCreate(savedInstanceState)
        init()

        initRv()
        setData()

        cvb?.swip?.setOnRefreshListener {
            latlon?.run {
                viewModel?.requestNearBy(page,latitude,longitude)
            }
            cvb?.swip?.isRefreshing = false
        }

        cvb?.ivCode?.setOnClickListener {
            if (PermissionChecker.hasPermissions(context, *PermissionChecker.CAMERA_PERMISSIONS)) {
                startCaptureActivity(scanKey,100)
            } else {
                PermissionChecker.requestPermissions(
                    context as Activity,
                    PermissionChecker.REQUEST_CODE_PERMISSION_CAMERA,
                    *PermissionChecker.CAMERA_PERMISSIONS
                )
            }
        }

        ScanManager.registerScanResultCallBack(scanKey, object : IScanResultCallBack {
            override fun result(result: String?) {
                result?.run {
                    if (result.isNotEmpty()) {
                        showLoading("")
                        viewModel?.validationSignIn(result)
                    } else {
                        ToastUtils.showLong("签到失败")
                    }
                    ActivityUtils.pop()
                }
            }

        })

        viewModel?.validation?.observe(viewLifecycleOwner, Observer {
            dismissLoading()
            if(!StringUtils.isEmpty(it)){
                //跳转
                startToReceiveActivity(it)
            }else{
                ToastUtils.showLong("签到失败")
            }
        })

        // 设置可视范围变化时的回调的接口方法
        aMap!!.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChange(position: CameraPosition) {}
            override fun onCameraChangeFinish(postion: CameraPosition) {
                //获取当前坐标
                val proj = aMap!!.projection
                cvb?.ivLocation?.run {
                    var width: Int = x.roundToInt()
                    var height : Int = y.roundToInt()
                    LogUtils.eTag("SignInLocationActivity",width)
                    LogUtils.eTag("SignInLocationActivity",height)
                    val latLng1: LatLng = proj.fromScreenLocation(
                        Point( width,
                        height)
                    )
                    this@PinFragment.latlon = latLng1
                    viewModel?.requestNearBy(page,latLng1.latitude,latLng1.longitude)
                }


            }
        })
    }


    override fun onFragmentVisible() {
        super.onFragmentVisible()
        latlon?.run {
            viewModel?.requestNearBy(page,latitude,longitude)
        }
    }
    /**
     * 方法必须重写
     */
    override fun onResume() {
        super.onResume()
        cvb?.map?.onResume()
        useMoveToLocationWithMapMode = true
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        cvb?.map?.onPause()
        useMoveToLocationWithMapMode = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        cvb?.map?.onSaveInstanceState(outState)
    }

    /**
     * 方法必须重写
     */
    override fun onDestroy() {
        super.onDestroy()
        cvb?.map?.onDestroy()
        mlocationClient?.onDestroy()
    }


    private val BACK_LOCATION_PERMISSION = "android.permission.ACCESS_BACKGROUND_LOCATION"

    override fun afterViewCreated() {
        if (PermissionChecker.hasPermissions(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                BACK_LOCATION_PERMISSION
            )
        ) {

        } else{
            PermissionChecker.requestPermissions(
                this,
                PermissionChecker.REQUEST_CODE_PERMISSION_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                BACK_LOCATION_PERMISSION
            )
        }
    }

    override fun activate(listener: OnLocationChangedListener?) {
        mListener = listener
        if (mlocationClient == null) {
            try {
                mlocationClient = AMapLocationClient(this.context)
                mLocationOption = AMapLocationClientOption()
                //设置定位监听
                mlocationClient?.setLocationListener(this)
                //设置为高精度定位模式
                mLocationOption?.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy)
                //是指定位间隔
                mLocationOption?.setInterval(2000)
                //设置定位参数
                mlocationClient?.setLocationOption(mLocationOption)
                // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
                // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
                // 在定位结束后，在合适的生命周期调用onDestroy()方法
                // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
                mlocationClient?.startLocation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun deactivate() {
        mListener = null
        if (mlocationClient != null) {
            mlocationClient!!.stopLocation()
            mlocationClient!!.onDestroy()
        }
        mlocationClient = null
    }

    override fun onLocationChanged(amapLocation: AMapLocation?) {
        Log.e("Amap", "onLocationChanged")
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                && amapLocation.getErrorCode() == 0
            ) {
                val latLng = LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())
                Location.currentLatlng = latLng
                EventBus.getDefault().post(LocationChangedEvent(latLng))
                Location.currentAddress = amapLocation.address
                //展示自定义定位小蓝点
                if (!firstLocation && PermissionChecker.hasPermissions(
                        context,  Manifest.permission.ACCESS_COARSE_LOCATION
                    )) {
                    //首次定位
                    firstLocation = true
                    //首次定位,选择移动到地图中心点并修改级别到15级
                    viewModel.requestNearBy(1,Location.currentLatlng!!.latitude,Location.currentLatlng!!.longitude)
                    aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                } else {
//                    if (useMoveToLocationWithMapMode) {
//                        //二次以后定位，使用sdk中没有的模式，让地图和小蓝点一起移动到中心点（类似导航锁车时的效果）
//                        startMoveLocationAndMap(latLng)
//                    } else {
//                        startChangeLocation(latLng)
//                    }
                }
            } else {
                val errText =
                    "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo()
                Log.e("AmapErr", errText)
            }
        }
    }

    override fun onTouch(p0: MotionEvent?) {
        useMoveToLocationWithMapMode = false
    }
    private var page = 1
    private var latlon:LatLng ?= null
    /**
     * 初始化
     */
    fun init() {
        if (aMap == null) {
            aMap = cvb?.map?.getMap()
            setUpMap()
        }
        cvb?.ivCurrentLocation?.setOnClickListener {
            Location.currentLatlng?.run {
                latlon = this
                aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(this, 15f))
                viewModel?.requestNearBy(page,latitude,longitude)
            }
        }
    }

    /**
     * 设置一些amap的属性
     */
    private fun setUpMap() {
        aMap?.setLocationSource(this) // 设置定位监听
        aMap?.setOnMarkerClickListener(this)
        aMap?.uiSettings?.setZoomControlsEnabled(false)
        aMap?.uiSettings?.isMyLocationButtonEnabled = false // 设置默认定位按钮是否显示
        aMap?.isMyLocationEnabled = true // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //aMap?.setOnMapTouchListener(this)
    }

    private fun initRv() {
        mAdapter = PinNftListAdapter().apply {
            setOnLoadMoreListener({
                //viewModel?.loadMore()
                mAdapter?.loadMoreEnd(false)
            }, cvb?.rvList)

            setOnItemClickListener { adapter, view, position ->
                startToReceiveActivity(adapter.getItem(position) as NearBy)
            }
        }

        cvb?.rvList?.run {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

    }

    private fun setData(){
        viewModel?.nearByData?.observe(viewLifecycleOwner, {
            mAdapter?.setNewData(it)
            addMarkets(it as ArrayList<NearBy>)
        })
    }

    /**
     * 修改自定义定位小蓝点的位置
     * @param latLng
     */
//    private fun startChangeLocation(latLng: LatLng) {
//        if (locationMarker != null) {
//            val curLatlng: LatLng? = locationMarker?.getPosition()
//            if (curLatlng == null || curLatlng != latLng) {
//                locationMarker?.setPosition(latLng)
//            }
//        }
//    }

    /**
     * 同时修改自定义定位小蓝点和地图的位置
     * @param latLng
     */
//    private fun startMoveLocationAndMap(latLng: LatLng) {
//
//        //将小蓝点提取到屏幕上
//        if (projection == null) {
//            projection = aMap!!.projection
//        }
//        if (locationMarker != null && projection != null) {
//            val markerLocation: LatLng? = locationMarker?.getPosition()
//            val screenPosition = aMap!!.projection.toScreenLocation(markerLocation)
//            locationMarker?.setPositionByPixels(screenPosition.x, screenPosition.y)
//        }
//
//        //移动地图，移动结束后，将小蓝点放到放到地图上
//        myCancelCallback.setTargetLatlng(latLng)
//        //动画移动的时间，最好不要比定位间隔长，如果定位间隔2000ms 动画移动时间最好小于2000ms，可以使用1000ms
//        //如果超过了，需要在myCancelCallback中进行处理被打断的情况
//        aMap!!.animateCamera(CameraUpdateFactory.changeLatLng(latLng), 1000, myCancelCallback)
//    }


//    var myCancelCallback: MyCancelCallback =
//        MyCancelCallback(locationMarker)

//    /**
//     * 监控地图动画移动情况，如果结束或者被打断，都需要执行响应的操作
//     */
//    class MyCancelCallback : CancelableCallback {
//        var targetLatlng: LatLng ? = null
//        var locationMarker: Marker? = null
//        constructor( locationMarker: Marker?){
//            this.locationMarker = locationMarker
//        }
//        @JvmName("setTargetLatlng1")
//        fun setTargetLatlng(latlng: LatLng?) {
//            targetLatlng = latlng
//        }
//
//        override fun onFinish() {
//            if (locationMarker != null && targetLatlng != null) {
//                locationMarker?.setPosition(targetLatlng)
//            }
//        }
//
//        override fun onCancel() {
//            if (locationMarker != null && targetLatlng != null) {
//                locationMarker?.setPosition(targetLatlng)
//            }
//        }
//    }

    private fun addMarkets(array: ArrayList<NearBy>){
        aMap?.clear()
        aMap?.run {
            for (index in 0 until array.size){
                //创建一个BitmapDescriptor数组
                val pic = arrayOf<BitmapDescriptor?>(null)
                returnPictureView(array[index].cid,object :ResultListener{
                    override fun onReturnResult(view: View?) {
                        view?.run {
                            pic[0] = BitmapDescriptorFactory.fromView(view);
                            putDataToMarkerOptions(pic[0],array[index])
                        }

                    }

                })

            }

        }

    }

    //在地图上进行标记
    private fun putDataToMarkerOptions(pic: BitmapDescriptor?, nearByFriend: NearBy) {
        var latLon = LatLng(nearByFriend.lat, nearByFriend.lng)
        var markerOption: MarkerOptions =
            MarkerOptions().icon(pic)
                .snippet(nearByFriend.giftId)
                .position(latLon)
                .draggable(true)
        var marker: Marker = aMap!!.addMarker(markerOption)

        val animation: Animation = ScaleAnimation(0F, 1F, 0f, 1f)
        animation.setInterpolator(LinearInterpolator())
        //整个移动所需要的时间
        animation.setDuration(500)
        //设置动画
        marker.setAnimation(animation)
        //开始动画
        marker.startAnimation()
    }

    //将图片加载到布局中
    private fun returnPictureView(
        imagUrl: String,
        resultListener: ResultListener
    ) {
        val markerView: View = LayoutInflater.from(context).inflate(R.layout.avator_view, null)
        val backGround: ImageView =
            markerView.findViewById<View>(R.id.iv_pic) as ImageView
        Glide.with(this)
            .asBitmap()
            .load(imagUrl)
            .transform(GlideCircleTransform())
            .error(R.drawable.ic_holder2)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    backGround.setImageBitmap(resource)
                    resultListener.onReturnResult(markerView)
                }
            })
    }

    //回调接口
    private interface ResultListener {
        fun onReturnResult(view: View?)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        marker?.run {
            LogUtils.eTag("PinFragment", "点击啦 $snippet")
            //通过snippet进去详情
            mAdapter?.run {
                var array = data
                var clickNearBy:NearBy ?= null
                for (index in 0 until array.size){
                    if(array[index].giftId.equals(snippet)){
                        clickNearBy = array[index]
                        break
                    }
                }
                clickNearBy?.run {
                    LogUtils.eTag("PinFragment", "startToReceiveActivity")
                    startToReceiveActivity(this)
                    return  true
                }

            }
        }
        return  true
    }

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        TODO("Not yet implemented")
    }

}