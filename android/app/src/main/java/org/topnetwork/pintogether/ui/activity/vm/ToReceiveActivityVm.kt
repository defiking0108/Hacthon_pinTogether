package org.topnetwork.pintogether.ui.activity.vm

import androidx.lifecycle.MutableLiveData
import com.topnetwork.net.network.Response
import org.topnetwork.pintogether.AppData
import org.topnetwork.pintogether.base.app.ToolbarBaseActivityVm
import org.topnetwork.pintogether.map.Location
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.request.giftPage
import org.topnetwork.pintogether.request.receive

class ToReceiveActivityVm : ToolbarBaseActivityVm(){
    var callback = MutableLiveData<Boolean>()
    var validation = MutableLiveData<Boolean>()
    var details = MutableLiveData<NearBy>()
    var check =  MutableLiveData<Boolean>()

    fun getDetails(id:String){
        giftPage(null,id,1,object: Response.Result<List<NearBy>>() {
            override fun succeeded(result: List<NearBy>) {
                super.succeeded(result)
                details.postValue(result[0])
            }

            override fun failed(message: String?) {
                super.failed(message)
                showToastMsgLiveData.postValue(message)
            }
        })
    }

    fun checkRequest(id:String){
        org.topnetwork.pintogether.request.check(AppData.address,id,object: Response.Result<String>(){
            override fun succeeded() {
                super.succeeded()
                check.postValue(true)
            }

            override fun succeeded(result: String) {
                super.succeeded(result)
                check.postValue(true)
            }

            override fun failed(message: String?) {
                super.failed(message)
                check.postValue(false)
            }
        })
    }

    fun validationSignIn(giftId: String){
        giftPage(null,giftId,1,object: Response.Result<List<NearBy>>() {
            override fun succeeded() {
                super.succeeded()
                validation.postValue(false)
            }
            override fun succeeded(result: List<NearBy>) {
                super.succeeded(result)
                validation.postValue(true)
            }

            override fun failed(message: String?) {
                super.failed(message)
                validation.postValue(false)
                showToastMsgLiveData.postValue(message)
            }
        })
    }
    fun toReceive(giftId:String){
        Location.currentLatlng?.run {
            receive(AppData.address,Location.currentAddress,giftId,latitude.toString(),longitude.toString(),
                object : Response.Result<String>(){
                    override fun succeeded(result: String) {
                        super.succeeded(result)
                        callback.postValue(true)
                    }

                    override fun succeeded() {
                        super.succeeded()
                        callback.postValue(true)
                    }

                    override fun failed(message: String?) {
                        super.failed(message)
                        callback.postValue(false)
                        message?.run {
                            showToastMsgLiveData.postValue(this)
                        }
                    }
                })
        }

    }

}