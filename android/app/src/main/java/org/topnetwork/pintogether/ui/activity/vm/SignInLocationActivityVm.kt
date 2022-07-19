package org.topnetwork.pintogether.ui.activity.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.topnetwork.net.network.Response
import org.topnetwork.pintogether.AppData
import org.topnetwork.pintogether.base.app.BaseActivityVM
import org.topnetwork.pintogether.request.createGift

class SignInLocationActivityVm : BaseActivityVM() {
    var create = MutableLiveData<String>()
    fun create(
        id:String,
        address:String,
        cid: String,
        name: String,
        description: String,
        num: String,
        ranges: String,
        sign: Boolean,
        lat: String,
        lon: String,
        numLimit:Boolean
    ) {
        createGift(
            id,AppData.address,address, cid, lat, lon, name, description,
            num, ranges, sign, numLimit,object : Response.Result<String>() {
                override fun succeeded() {
                    super.succeeded()
                    create.postValue("")
                }

                override fun succeeded(result: String) {
                    super.succeeded(result)
                    Log.e("CreateNftActivityVm", "succeeded")
                    create.postValue(result)
                }

                override fun failed(message: String?) {
                    super.failed(message)
                    Log.e("CreateNftActivityVm", "failed")
                    showToastMsgLiveData.postValue(message)
                    create.postValue("")
                }
            })

    }
}