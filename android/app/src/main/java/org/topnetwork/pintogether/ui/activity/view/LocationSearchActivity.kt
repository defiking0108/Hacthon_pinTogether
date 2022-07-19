package org.topnetwork.pintogether.ui.activity.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.maps.model.LatLng
import com.amap.api.services.core.AMapException
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.help.Tip
import org.greenrobot.eventbus.EventBus
import org.topnetwork.pintogether.AppData
import org.topnetwork.pintogether.R
import org.topnetwork.pintogether.adapter.LocationSearchAdapter
import org.topnetwork.pintogether.base.app.BaseActivity
import org.topnetwork.pintogether.databinding.ActivityLocationSearchBinding
import org.topnetwork.pintogether.event.LocationSearchEvent
import org.topnetwork.pintogether.model.LocationSearch
import org.topnetwork.pintogether.startNftDetails
import org.topnetwork.pintogether.ui.activity.vm.LocationSearchActivityVm
import java.util.ArrayList
import java.util.HashMap

class LocationSearchActivity :
    BaseActivity<ActivityLocationSearchBinding, LocationSearchActivityVm>(), TextWatcher,
    Inputtips.InputtipsListener {
    private var mAdapter:LocationSearchAdapter ?= null
    override val layoutResId: Int
        get() = R.layout.activity_location_search

    override fun createViewModel(): LocationSearchActivityVm = ViewModelProvider(this).get(
        LocationSearchActivityVm::class.java
    )

    override fun afterOnCreate(savedInstanceState: Bundle?) {
        super.afterOnCreate(savedInstanceState)
        initRv()
        cvb?.editSearch?.addTextChangedListener(this)
        cvb?.tvCancel?.setOnClickListener {
            finish()
        }
        cvb?.ivDelete?.setOnClickListener {
            cvb?.editSearch?.setText("")
        }
    }

    private fun initRv(){
        mAdapter = LocationSearchAdapter()
        mAdapter?.run {
            setOnItemClickListener { adapter, _, position ->
                val item: LocationSearch = adapter.getItem(position) as LocationSearch
                EventBus.getDefault().post(LocationSearchEvent(item.latLng))
                finish()
            }
        }
        cvb?.rv?.run {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        val newText = s.toString().trim { it <= ' ' }
        val inputquery = InputtipsQuery(newText, AppData.city)
        inputquery.cityLimit = true
        val inputTips = Inputtips(this@LocationSearchActivity, inputquery)
        inputTips.setInputtipsListener(this)
        inputTips.requestInputtipsAsyn()
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun onGetInputtips(tipList: MutableList<Tip>?, rCode: Int) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            var array:ArrayList<LocationSearch> = arrayListOf()
            val listString: MutableList<HashMap<String, String?>> = ArrayList()
            if (tipList != null) {
                val size: Int = tipList.size
                for (i in 0 until size) {
                    val tip: Tip = tipList.get(i)
                    if (tip != null) {
                        val map = HashMap<String, String?>()
                        map["name"] = tipList.get(i).getName()
                        map["address"] = tipList.get(i).getDistrict()
                        listString.add(map)
                        var localSearch = LocationSearch()
                        localSearch.name = tipList.get(i).getName()
                        localSearch.address = tipList.get(i).getDistrict()
                        localSearch.latLng = LatLng(tipList.get(i).point.latitude,tipList.get(i).point.longitude)
                        array.add(localSearch)
                        mAdapter?.setNewData(array)
                    }
                }
            }
        } else {

        }

    }

}