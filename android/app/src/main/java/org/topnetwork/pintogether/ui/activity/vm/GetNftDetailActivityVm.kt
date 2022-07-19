package org.topnetwork.pintogether.ui.activity.vm

import androidx.lifecycle.MutableLiveData
import org.topnetwork.pintogether.base.app.ToolbarBaseActivityVm
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.request.giftPage

class GetNftDetailActivityVm : ToolbarBaseActivityVm() {
    var details = MutableLiveData<NearBy>()

    fun getDetails(account:String?,id:String?,nearBy: NearBy?){
        if(nearBy != null){
            nearBy.run {
                details.postValue(this)
            }
        }else{
            giftPage(account,id,1,object: com.topnetwork.net.network.Response.Result<List<NearBy>>() {
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

    }
}