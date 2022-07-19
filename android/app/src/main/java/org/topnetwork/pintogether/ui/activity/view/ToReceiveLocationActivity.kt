package org.topnetwork.pintogether.ui.activity.view

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.AMap
import com.amap.api.maps.AMap.*
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapsInitializer
import com.amap.api.maps.model.*

import com.amap.api.maps.model.animation.Animation
import org.greenrobot.eventbus.EventBus
import org.topnetwork.pintogether.*
import org.topnetwork.pintogether.base.app.BaseActivity

import org.topnetwork.pintogether.databinding.ActivityToReceiveLocationBinding
import org.topnetwork.pintogether.ui.activity.vm.ToReceiveLocationActivityVm
import com.amap.api.maps.model.animation.ScaleAnimation
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.route.*
import com.amap.api.services.route.RouteSearch.WalkRouteQuery
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.topnetwork.pintogether.event.LocationChangedEvent
import org.topnetwork.pintogether.event.SignInLocationEvent
import org.topnetwork.pintogether.map.Location
import org.topnetwork.pintogether.utils.LogUtils
import org.topnetwork.pintogether.utils.WalkRouteOverlay
import org.topnetwork.pintogether.utils.image.transform.GlideCircleTransform


/**
 *  作者    lgc
 *  时间    2022/7/14 18:34
 *  文件    PinTogether
 *  描述    领取位置
 */
class ToReceiveLocationActivity:
    BaseActivity<ActivityToReceiveLocationBinding, ToReceiveLocationActivityVm>() ,
    AMapLocationListener,AMap.OnMarkerClickListener,OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter,
    RouteSearch.OnRouteSearchListener {

    private var circle: Circle? = null
    private var polygon: Polygon? = null
    private var radius:Double ?= null
    private var mRouteSearch: RouteSearch? = null
    var fromLatlng:LatLng ?= null
    var toLatlng:LatLng ?= null
    var pic :String ?= null

    //0修改,1没有修改
    private var aMap: AMap? = null
    override val layoutResId: Int
        get() = R.layout.activity_to_receive_location

    override fun createViewModel(): ToReceiveLocationActivityVm = ViewModelProvider(this).get(
        ToReceiveLocationActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        intent.extras?.run {
            fromLatlng = LatLng(getDouble(key_receive_location_from_lat),getDouble(
                key_receive_location_from_lon))

            toLatlng = LatLng(getDouble(key_receive_location_to_lat),getDouble(
                key_receive_location_to_lon))
            pic = getString(key_receive_location_pic)
            radius = getDouble(key_receive_location_radius)
        }

        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
        cvb?.map?.onCreate(savedInstanceState)

        init()

        cvb?.tvCancel?.setOnClickListener {
            finish()
        }

        cvb?.ivCurrentLocation?.setOnClickListener {
            Location.currentLatlng?.run {
                aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,longitude), 15f))
            }
        }
    }

    /**
     * 初始化
     */
    private fun init() {
        if (aMap == null) {
            aMap = cvb?.map?.getMap()
            setUpMap()
        }

    }

    private val STROKE_COLOR: Int = Color.argb(0, 0, 0, 0)
    private val FILL_COLOR: Int = Color.argb(0, 0, 0, 0)

    /**
     * 设置一些amap的属性
     */
    private fun setUpMap() {
        aMap?.uiSettings?.setZoomControlsEnabled(false)
        aMap?.uiSettings?.isMyLocationButtonEnabled = false // 设置默认定位按钮是否显示
        aMap?.isMyLocationEnabled = false // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //aMap?.setOnMapTouchListener(this)
        aMap?.setOnMarkerClickListener(this)
        aMap!!.setOnMapClickListener(this)
        aMap!!.setOnMarkerClickListener(this)
        aMap!!.setOnInfoWindowClickListener(this)
        aMap!!.setInfoWindowAdapter(this)
        //gift的地点
        aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(fromLatlng, 15f))

        try {
            mRouteSearch = RouteSearch(this)
            mRouteSearch?.setRouteSearchListener(this)
        } catch (e: AMapException) {
            e.printStackTrace()
        }

        val markerView: View = LayoutInflater.from(this).inflate(R.layout.avator_view, null)
        val backGround: ImageView =
            markerView.findViewById<View>(R.id.iv_pic) as ImageView
        Glide.with(this)
            .asBitmap()
            .load(pic)
            .transform(GlideCircleTransform())
            .error(R.drawable.ic_holder2)
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    backGround.setImageBitmap(resource)
                    var des = BitmapDescriptorFactory.fromView(markerView)

                    var markerOption: MarkerOptions =
                        MarkerOptions().icon(des)
                            .position(fromLatlng)
                            .draggable(true)
                    var marker: Marker = aMap!!.addMarker(markerOption)

                    val animation: Animation = ScaleAnimation(0F, 1F, 0f, 1f)
                    animation.setInterpolator(LinearInterpolator())
                    //整个移动所需要的时间
                    //整个移动所需要的时间
                    animation.setDuration(1000)
                    //设置动画
                    //设置动画
                    marker.setAnimation(animation)
                    //开始动画
                    //开始动画
                    marker.startAnimation()

//                    // 自定义系统定位蓝点
//                    // 自定义系统定位蓝点
//                    val myLocationStyle = MyLocationStyle()
//                    // 自定义定位蓝点图标（此处替换成本身的icon图标就能够了）
//                    // 自定义定位蓝点图标（此处替换成本身的icon图标就能够了）
//                    myLocationStyle.myLocationIcon(des)
//                    // 自定义精度范围的圆形边框颜色
//                    // 自定义精度范围的圆形边框颜色
//                    myLocationStyle.strokeColor(STROKE_COLOR)
//                    //自定义精度范围的圆形边框宽度
//                    //自定义精度范围的圆形边框宽度
//                    myLocationStyle.strokeWidth(5f)
//                    // 设置圆形的填充颜色
//                    // 设置圆形的填充颜色
//                    myLocationStyle.radiusFillColor(FILL_COLOR)
//                    // 将自定义的 myLocationStyle 对象添加到地图上
//                    // 将自定义的 myLocationStyle 对象添加到地图上
//                    aMap!!.myLocationStyle = myLocationStyle
                }
            })
        addMarket1()
        val circleOptions = radius?.let {
            CircleOptions().center(fromLatlng)
                .radius(it).strokeColor(Color.parseColor("#293665fd"))
                .fillColor(Color.parseColor("#293665fd")).strokeWidth(0f)
        }
        circle = aMap!!.addCircle(circleOptions)

    }

    fun addMarket1(){
        //添加领取人的位置
        var latLon = toLatlng

        var pic = BitmapDescriptorFactory.fromResource(R.drawable.ic_sign_in_location)
        var markerOption: MarkerOptions =
            MarkerOptions().icon(pic)
                .position(latLon)
                .draggable(true)
        var marker: Marker = aMap!!.addMarker(markerOption)

        val animation: Animation = ScaleAnimation(0F, 1F, 0f, 1f)
        animation.setInterpolator(LinearInterpolator())
        //整个移动所需要的时间
        //整个移动所需要的时间
        animation.setDuration(1000)
        //设置动画
        //设置动画
        marker.setAnimation(animation)
        //开始动画
        //开始动画
        marker.startAnimation()
    }

    /**
     * 方法必须重写
     */
    override fun onResume() {
        super.onResume()
        cvb?.map?.onResume()
    }

    /**
     * 方法必须重写
     */
    override fun onPause() {
        super.onPause()
        cvb?.map?.onPause()
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
        EventBus.getDefault().unregister(this)
    }

    override fun onLocationChanged(p0: AMapLocation?) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: LocationChangedEvent) {

    }

    override fun onMarkerClick(p0: Marker?): Boolean {
//        // 步行路径规划
//        if(toLatlng != null && fromLatlng != null){
//            val fromAndTo = RouteSearch.FromAndTo(
//                LatLonPoint(toLatlng!!.latitude,toLatlng!!.longitude),
//                        LatLonPoint(fromLatlng!!.latitude,fromLatlng!!.longitude)
//            )
//            val query = WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault)
//            mRouteSearch?.calculateWalkRouteAsyn(query) // 异步路径规划步行模式查询
//        }

        return true
    }

    override fun onMapClick(p0: LatLng?) {

    }

    override fun onInfoWindowClick(p0: Marker?) {

    }

    override fun getInfoWindow(p0: Marker?): View? {
       return  null
    }

    override fun getInfoContents(p0: Marker?): View? {
        return  null
    }

    override fun onBusRouteSearched(p0: BusRouteResult?, p1: Int) {

    }

    override fun onDriveRouteSearched(p0: DriveRouteResult?, p1: Int) {

    }
    private var mWalkRouteResult: WalkRouteResult? = null
    override fun onWalkRouteSearched(result: WalkRouteResult?, errorCode: Int) {
        aMap!!.clear() // 清理地图上的所有覆盖物

        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size > 0) {
                    mWalkRouteResult = result
                    val walkPath: WalkPath = mWalkRouteResult!!.getPaths()
                        .get(0) ?: return
                    val walkRouteOverlay = WalkRouteOverlay(
                        this, aMap, walkPath,
                        mWalkRouteResult!!.getStartPos(),
                        mWalkRouteResult!!.getTargetPos()
                    )
                    walkRouteOverlay.removeFromMap()
                    walkRouteOverlay.addToMap()
                    walkRouteOverlay.zoomToSpan()
//                    mBottomLayout.setVisibility(View.VISIBLE)
                    val dis = walkPath.distance.toInt()
                    val dur = walkPath.duration.toInt()
//                    val des: String =
//                        AMapUtil.getFriendlyTime(dur).toString() + "(" + AMapUtil.getFriendlyLength(
//                            dis
//                        ) + ")"
//                    mRotueTimeDes.setText(des)
//                    mRouteDetailDes.setVisibility(View.GONE)
//                    mBottomLayout.setOnClickListener(View.OnClickListener {
//                        val intent = Intent(
//                            mContext,
//                            WalkRouteDetailActivity::class.java
//                        )
//                        intent.putExtra("walk_path", walkPath)
//                        intent.putExtra(
//                            "walk_result",
//                            mWalkRouteResult
//                        )
//                        startActivity(intent)
//                    })
                } else if (result != null && result.getPaths() == null) {
                    //ToastUtil.show(mContext, R.string.no_result)
                }
            } else {
                //ToastUtil.show(mContext, R.string.no_result)
            }
        } else {
            //ToastUtil.showerror(this.applicationContext, errorCode)
        }
    }

    override fun onRideRouteSearched(p0: RideRouteResult?, p1: Int) {

    }

}