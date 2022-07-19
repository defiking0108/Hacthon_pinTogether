package org.topnetwork.pintogether.ui.activity.view

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.AMap.OnCameraChangeListener
import com.amap.api.maps.AMap.OnMapLoadedListener
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.animation.Animation
import com.amap.api.maps.model.animation.TranslateAnimation
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.android.synthetic.main.activity_sign_in_location.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.topnetwork.pintogether.*
import org.topnetwork.pintogether.adapter.SignInLocationAdapter
import org.topnetwork.pintogether.base.NormalBaseConfig
import org.topnetwork.pintogether.base.app.BaseActivity
import org.topnetwork.pintogether.databinding.ActivitySignInLocationBinding
import org.topnetwork.pintogether.event.LocationSearchEvent
import org.topnetwork.pintogether.event.SignInLocationEvent
import org.topnetwork.pintogether.map.Location
import org.topnetwork.pintogether.model.LocationSearch
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.ui.activity.vm.SignInLocationActivityVm
import org.topnetwork.pintogether.utils.LogUtils
import org.topnetwork.pintogether.utils.StringUtils
import org.topnetwork.pintogether.widgets.picker.builder.OptionsPickerBuilder
import org.topnetwork.pintogether.widgets.picker.view.OptionsPickerView
import java.lang.Exception
import kotlin.math.roundToInt

class SignInLocationActivity :
    BaseActivity<ActivitySignInLocationBinding, SignInLocationActivityVm>(), LocationSource,PoiSearch.OnPoiSearchListener
    ,AMapLocationListener, OnGeocodeSearchListener {
    private var mAdapter:SignInLocationAdapter ?= null
    private var currentLatLng: LatLng ?= null
    private var surroundingLatLng: LatLng ?= null
    private var fromLatlng: LatLng ?= null

    private var isInitLocation = false
    private var currentLocation : LocationSearch ?= null
    private var currentLocation1 : LocationSearch ?= null

    private var poiResult // poi返回的结果
            : PoiResult? = null
    private var currentPage = 0 // 当前页面，从0开始计数
    private var poiItems // poi数据
            : List<PoiItem>? = null

    private var query // Poi查询条件类
            : PoiSearch.Query? = null
    private var aMap: AMap? = null
    private var mListener: LocationSource.OnLocationChangedListener? = null
    private var mlocationClient: AMapLocationClient? = null
    private var mLocationOption: AMapLocationClientOption? = null

    var isChange:Boolean = false
    var nearBy:NearBy ?= null

    override val layoutResId: Int
        get() = R.layout.activity_sign_in_location

    override fun createViewModel(): SignInLocationActivityVm = ViewModelProvider(this).get(
        SignInLocationActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        EventBus.getDefault().register(this)
        MapsInitializer.updatePrivacyShow(this, true, true)
        MapsInitializer.updatePrivacyAgree(this, true)
        cvb?.map?.onCreate(savedInstanceState)

        intent.extras?.run {
            fromLatlng = LatLng(getDouble(key_receive_location_from_lat),getDouble(
                key_receive_location_from_lon
            ))

            fromLatlng?.run {
                var string = "$latitude  $longitude"
                LogUtils.eTag("SignInLocationActivity",string)
            }
            isChange = getBoolean(key_is_change)
            if(isChange){
                nearBy = getParcelable(key_near_by)
                cvb?.tvScope?.text = nearBy?.ranges + "米"
            }

        }

        init()
        initRv()
    }

    private var geocoderSearch: GeocodeSearch?= null
    /**
     * 初始化
     */
    private fun init() {
        if (aMap == null) {
            aMap = cvb?.map?.getMap()

            aMap!!.setOnMapLoadedListener {
                LogUtils.eTag("SignInLocationActivity","OnMapLoaded")
            }

            try {
                geocoderSearch = GeocodeSearch(this)
                geocoderSearch?.setOnGeocodeSearchListener(this)
            } catch (e: AMapException) {
                e.printStackTrace()
            }
            cvb?.llSearch?.setOnClickListener {
                startLocationSearchActivity()
            }
            cvb?.tvCancel?.setOnClickListener {
                finish()
            }

            cvb?.ivCurrentLocation?.setOnClickListener {
                Location.currentLatlng?.run {
                    aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitude,longitude), 15f))
                }
            }
            viewModel?.create?.observe(this, Observer {
                dismissLoading()
                if(!StringUtils.isEmpty(it)){
                    var array = mAdapter?.data
                    array?.run {
                        for (i in 0 until size) {
                            if(get(i).isSelect){
                                var item = get(i)
                                var rangesStr = cvb?.tvScope?.text
                                var ranges = cvb?.tvScope?.text?.substring(0,rangesStr!!.length - 1)
                                var event  = SignInLocationEvent(item,ranges)
                                EventBus.getDefault().post(event)
                                //回调回去
                                finish()
                            }
                        }
                    }

                }
            })
            cvb?.tvConfirm?.setOnClickListener {
                if(isChange){
                    //得到当前选中的地址
                    var array = mAdapter?.data
                    array?.run {
                        for (i in 0 until size) {
                            if(get(i).isSelect){
                                var item = get(i)
                                var rangesStr = cvb?.tvScope?.text
                                var ranges = cvb?.tvScope?.text?.substring(0,rangesStr!!.length - 1)
                                //网络请求更新
                                nearBy?.run {
                                    showCancelLoading("")
                                    viewModel?.create(giftId,item.address,cid,name,description,num,ranges!!,isSign
                                        ,item.latLng.latitude.toString(),item.latLng.longitude.toString(), isNumLimit)

                                }


                            }
                        }
                    }
                }else{
                    //得到当前选中的地址
                    var array = mAdapter?.data
                    array?.run {
                        for (i in 0 until size) {
                            if(get(i).isSelect){
                                var item = get(i)
                                var rangesStr = cvb?.tvScope?.text
                                var ranges = cvb?.tvScope?.text?.substring(0,rangesStr!!.length - 1)
                                var event  = SignInLocationEvent(item,ranges)
                                EventBus.getDefault().post(event)
                                //回调回去
                                finish()
                            }
                        }
                    }
                }

            }

            cvb?.rlScope?.setOnClickListener {
                var stringList:ArrayList<String> = arrayListOf("50米","100米","200米","300米","400米","500米","600米","700米","800米","900米","1000米")
                val optionsPickerBuilder =
                    OptionsPickerBuilder(NormalBaseConfig.getContext()) { options1, option2, options3, v ->
                        cvb?.tvScope?.setText(stringList[options1])
                    }
                val mCurrencyUnitPickerView: OptionsPickerView<String> =
                    optionsPickerBuilder.setDecorView(cvb!!.rlRoot) //必须是RelativeLayout，不设置setDecorView的话，底部虚拟导航栏会显示在弹出的选择器区域
                        .setTitleText("") //标题文字
                        .setTitleSize(18) //标题文字大小
                        .setCancelText("取消") //取消按钮文字
                        .setSubmitText("确定") //确认按钮文字
                        .setContentTextSize(20) //滚轮文字大小
                        .setLineSpacingMultiplier(1.8f) //行间距
                        .setSelectOptions(0) //设置选择的值
                        .build()

                mCurrencyUnitPickerView.setPicker(stringList) //添加数据

                val size: Int = stringList.size
                for (i in 0 until size) {
                    if (cvb?.tvScope?.text.toString()
                            .equals(stringList.get(i))
                    ) {
                        mCurrencyUnitPickerView.setSelectOptions(i)
                        break
                    }
                }
                mCurrencyUnitPickerView.show()
            }

            // 设置可视范围变化时的回调的接口方法
            aMap!!.setOnCameraChangeListener(object : OnCameraChangeListener {
                override fun onCameraChange(position: CameraPosition) {}
                override fun onCameraChangeFinish(postion: CameraPosition) {
                    //获取当前坐标
                    val proj = aMap!!.projection
                    cvb?.ivLocation?.run {
                        var width: Int = x.roundToInt()
                        var height : Int = y.roundToInt()
                        LogUtils.eTag("SignInLocationActivity",width)
                        LogUtils.eTag("SignInLocationActivity",height)
                        val latLng: LatLng = proj.fromScreenLocation(Point( width,
                            height)
                        )
                        surroundingLatLng = latLng
                        surroundingLatLng?.run {
                            val query = RegeocodeQuery(
                                LatLonPoint(latitude,longitude), 0F,
                                GeocodeSearch.AMAP
                            ) // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                            geocoderSearch?.getFromLocationAsyn(query) // 设置异步逆地理编码请求

                        }
                    }


                }
            })

            setUpMap()
        }
    }

    private fun initRv(){
        mAdapter = SignInLocationAdapter()
        mAdapter?.run {
            setOnItemClickListener { adapter, _, position ->
                val item = adapter.getItem(position)
                var array = mAdapter?.data
                array?.run {
                    for (i in 0 until array.size) {
                        get(i).isSelect = false
                    }
                }
                (item as LocationSearch).isSelect = true
                mAdapter?.notifyDataSetChanged()
            }
        }
        cvb?.rv?.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    /**
     * 设置一些amap的属性
     */
    private fun setUpMap() {
        aMap?.setLocationSource(this) // 设置定位监听
        aMap?.uiSettings?.isZoomControlsEnabled = false
        aMap?.uiSettings?.isMyLocationButtonEnabled = false // 设置默认定位按钮是否显示
        aMap?.isMyLocationEnabled = true // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        //aMap?.setOnMapTouchListener(this)
    }

    override fun activate(listener: LocationSource.OnLocationChangedListener?) {
        mListener = listener
        if (mlocationClient == null) {
            try {
                mlocationClient = AMapLocationClient(this)
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
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                && amapLocation.getErrorCode() == 0
            ) {
                val objectMapper = ObjectMapper()
            LogUtils.eTag("SignInLocationActivity", objectMapper.writeValueAsString(amapLocation))
                val latLng = LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())
                currentLocation = LocationSearch(amapLocation)
                currentLatLng = latLng
                if(!isInitLocation){
                    surroundingLatLng = currentLatLng
                    currentLocation1 = currentLocation
                    fromLatlng?.run {
                        aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(this, 15f))
                        isInitLocation = true
                        doSearchQuery()
                    }

                }

                //展示自定义定位小蓝点
//                if (locationMarker == null) {
//                    //首次定位
//                    locationMarker = aMap!!.addMarker(
//                        MarkerOptions().position(latLng)
////                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
//                            .anchor(0.5f, 0.5f)
//                    )
//
//                    //首次定位,选择移动到地图中心点并修改级别到15级
//                    aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
//                }



            } else {
                val errText =
                    "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo()
                Log.e("AmapErr", errText)
            }
        }
    }

    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    /**
     * 开始进行poi搜索
     */
    protected fun doSearchQuery() {
        currentPage = 0
        query =
            PoiSearch.Query("", "", "") // 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query?.setPageSize(20) // 设置每页最多返回多少条poiitem
        query?.setPageNum(currentPage) // 设置查第一页
        surroundingLatLng?.run{
            try {
                val lp = LatLonPoint(longitude, latitude) // 116.472995,39.993743
                var poiSearch: PoiSearch? = null
                poiSearch = PoiSearch(this@SignInLocationActivity, query)
                poiSearch.setOnPoiSearchListener(this@SignInLocationActivity)
                poiSearch.setBound(PoiSearch.SearchBound(lp, 5000, true)) //
                // 设置搜索区域为以lp点为圆心，其周围5000米范围
                poiSearch.searchPOIAsyn() // 异步搜索
            } catch (e: AMapException) {
                e.printStackTrace()
            }
        }
    }

    override fun onPoiSearched(result: PoiResult?, rcode: Int) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery() == query) {// 是否是同一条
                    // 是否是同一条
                    poiResult = result
                    poiItems = poiResult?.getPois() // 取得第一页的poiitem数据，页数从数字0开始
                    poiResult?.run {
                        val suggestionCities = getSearchSuggestionCitys() // 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                        poiItems ?.run {
                            LogUtils.eTag("SignInLocationActivity",poiItems!!.size)
                            var array : ArrayList<LocationSearch> = ArrayList()
                            currentLocation1?.run {
                                array.add(this)
                            }
                            for (i in indices) {
                                var signInLocation = LocationSearch(get(i))
                                array.add(signInLocation)
                            }
                            mAdapter?.setNewData(array)
                        }
                    }



                }
            }
        }
    }

    override fun onPoiItemSearched(p0: PoiItem?, p1: Int) {

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
        mlocationClient?.onDestroy()
        EventBus.getDefault().unregister(this)
    }

    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getRegeocodeAddress() != null && result.getRegeocodeAddress()
                    .getFormatAddress() != null
            ) {
                            val objectMapper = ObjectMapper()
            LogUtils.eTag("SignInLocationAdapter", objectMapper.writeValueAsString(result.getRegeocodeAddress()))
            var signInLocation = LocationSearch(result.getRegeocodeAddress())
                currentLocation1 = signInLocation
                doSearchQuery()
//                addressName = (result.getRegeocodeAddress().getFormatAddress()
//                        + "附近")
//                aMap!!.animateCamera(
//                    CameraUpdateFactory.newLatLngZoom(
//                        AMapUtil.convertToLatLng(latLonPoint), 15f
//                    )
//                )
//                regeoMarker.setPosition(AMapUtil.convertToLatLng(latLonPoint))
//                ToastUtil.show(this@ReGeocoderActivity, addressName)
            } else {
                //ToastUtil.show(this@ReGeocoderActivity, R.string.no_result)
            }
        } else {
            //ToastUtil.showerror(this, rCode)
        }

    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: LocationSearchEvent) {
        surroundingLatLng = event.latLng
        aMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(surroundingLatLng, 15f))
        surroundingLatLng?.run {
            val query = RegeocodeQuery(
                LatLonPoint(latitude,longitude), 0F,
                GeocodeSearch.AMAP
            ) // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
            geocoderSearch?.getFromLocationAsyn(query) // 设置异步逆地理编码请求
        }
    }


}