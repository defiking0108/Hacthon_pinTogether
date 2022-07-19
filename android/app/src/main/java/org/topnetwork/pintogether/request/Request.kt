package org.topnetwork.pintogether.request

import com.topnetwork.net.network.RequestParam
import com.topnetwork.net.network.Response
import com.topnetwork.net.network.requestApiOnResult
import org.topnetwork.pintogether.api.Api
import org.topnetwork.pintogether.model.NearBy


fun createGift(giftId: String?, account:String?,address:String,cid:String?,lat: String, lng: String,
               name: String,description:String,num: String,ranges: String,sign:Boolean, numLimit:Boolean,result: Response.Result<String>) {
    val requestBody = RequestParam.Builder().apply {
        put("account", account)
        put("address", address)
        put("cid", cid)
        put("description", description)
        giftId?.run {
            put("giftId", giftId)
        }
        put("lat", lat)
        put("lng", lng)
        put("name", name)
        put("num", num)
        put("ranges", ranges)
        put("sign", sign)
        put("numLimit", numLimit)


    }.build().requestBodyNoEncode
    requestApiOnResult(Api.instance.createGift(requestBody), Response.ModelResult(result), result.isShowLoading)


}

fun nearBy(curlat:String,curlng:String,page:Int,result: Response.Result<List<NearBy>>){
    val requestBody = RequestParam.Builder().apply {
        put("curlat", curlat)
        put("curlng", curlng)
        put("pageIndex", page)
        put("pageSize", 20)
    }.build().requestBodyNoEncode
    requestApiOnResult(Api.instance.nearby(requestBody), Response.ModelResult(result), result.isShowLoading)
}

fun giftPage(account:String?,id:String?,page: Int, result: Response.Result<List<NearBy>>){
    val requestBody = RequestParam.Builder().apply {
        account?.run {
            put("account", account)
        }
        id?.run {
            put("id", id)
        }
        put("pageIndex", page)
        put("pageSize", 20)
    }.build().requestBodyNoEncode
    requestApiOnResult(Api.instance.giftPage(requestBody), Response.ModelResult(result), result.isShowLoading)
}

fun receivePage(account:String?,page: Int, result: Response.Result<List<NearBy>>){
    val requestBody = RequestParam.Builder().apply {
        account?.run {
            put("account", account)
        }
        put("pageIndex", page)
        put("pageSize", 20)
    }.build().requestBodyNoEncode
    requestApiOnResult(Api.instance.receivePage(requestBody), Response.ModelResult(result), result.isShowLoading)
}

fun receive(account:String,address: String,giftId:String,lat:String,lng:String, result: Response.Result<String>){
    val requestBody = RequestParam.Builder().apply {
        put("account", account)
        put("address", address)
        put("giftId",giftId)
        put("lat",lat)
        put("lng",lng)
    }.build().requestBodyNoEncode
    requestApiOnResult(Api.instance.receive(requestBody), Response.ModelResult(result), result.isShowLoading)
}

fun check(account:String,id:String,result: Response.Result<String>){
    val params = HashMap<String, String>().apply {

    }
    requestApiOnResult(Api.instance.check(account,id), Response.ModelResult(result), result.isShowLoading)
}



