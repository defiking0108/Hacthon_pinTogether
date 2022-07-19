package org.topnetwork.pintogether.ui.fragment.vm

import androidx.lifecycle.MutableLiveData
import com.topnetwork.net.network.Response
import org.topnetwork.pintogether.base.app.BaseFragmentVM
import org.topnetwork.pintogether.model.NearBy
import org.topnetwork.pintogether.request.receivePage

class GetNftFragmentVm : BaseFragmentVM(){
    var data = MutableLiveData<List<NearBy>>()

    //获取得到的nft
    fun getNftData(account:String,page:Int){
        receivePage(account,page,object: Response.Result<List<NearBy>>() {
            override fun succeeded() {
                super.succeeded()
                data.postValue(arrayListOf())
            }
            override fun succeeded(result: List<NearBy>) {
                super.succeeded(result)
                data.postValue(result)
            }

            override fun failed(message: String?) {
                super.failed(message)
                data.postValue(arrayListOf())
                message?.run {
                    showToastLiveData.postValue(this)
                }

            }
        })
    }
}