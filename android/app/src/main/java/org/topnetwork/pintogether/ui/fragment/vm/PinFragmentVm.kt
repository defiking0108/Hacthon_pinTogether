package org.topnetwork.pintogether.ui.fragment.vm

import androidx.lifecycle.MutableLiveData
import com.amap.api.maps.model.LatLng
import com.topnetwork.net.network.Response
import org.topnetwork.pintogether.base.app.BaseFragmentVM
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.request.giftPage
import org.topnetwork.pintogether.request.nearBy

class PinFragmentVm:BaseFragmentVM() {
    var validation = MutableLiveData<String>()
    fun validationSignIn(giftId: String){
        giftPage(null,giftId,1,object: Response.Result<List<NearBy>>() {
            override fun succeeded() {
                super.succeeded()
                validation.postValue("")
            }
            override fun succeeded(result: List<NearBy>) {
                super.succeeded(result)
                validation.postValue(giftId)
            }

            override fun failed(message: String?) {
                super.failed(message)
                validation.postValue("")
                showToastLiveData.postValue(message)
            }
        })
    }
    var nearByData = MutableLiveData<List<NearBy>>()
    fun requestNearBy(page:Int,latitude:Double,longitude:Double){
            nearBy(latitude.toString(),longitude.toString(),page,object :
                Response.Result<List<NearBy>>() {
                override fun succeeded() {
                    super.succeeded()
                }
                override fun succeeded(result: List<NearBy>) {
                    super.succeeded(result)
                    nearByData.postValue(result)
                }

                override fun failed(message: String?) {
                    super.failed(message)
                }
            })
    }
}