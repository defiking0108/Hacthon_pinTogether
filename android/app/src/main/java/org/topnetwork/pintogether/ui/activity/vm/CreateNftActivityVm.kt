package org.topnetwork.pintogether.ui.activity.vm

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.topnetwork.pintogether.AppData
import org.topnetwork.pintogether.base.app.ToolbarBaseActivityVm
import org.topnetwork.pintogether.model.CidModel
import org.topnetwork.pintogether.request.createGift
import java.io.File
import java.io.IOException

class CreateNftActivityVm : ToolbarBaseActivityVm() {
    var cid = MutableLiveData<String>()
    var create = MutableLiveData<String>()

    fun uploadFile(getPath: String, url: String) {
        var file = File(getPath);//文件路径
        var size: Long = file.length();//文件长度
        var httpClient = OkHttpClient()
        var mediaType: MediaType? = "image/*".toMediaTypeOrNull();//设置类型，类型为八位字节流
        var requestBody: RequestBody = RequestBody.create(mediaType, file);//把文件与类型放入请求体

        var multipartBody: MultipartBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", file.getName(), requestBody)//文件名,请求体里的文件
            .build()

        var request: Request = Request.Builder()
            .header(
                "Authorization",
                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJkaWQ6ZXRocjoweEEzMDU5ZTBmQUY0M2M3M2ZhMjNjQTBlNGJiMDc5ODI4ZWYwYTAwNzYiLCJpc3MiOiJuZnQtc3RvcmFnZSIsImlhdCI6MTY1NzU5NTUzNzUwMywibmFtZSI6InBpbnRvZ2V0aGVyIn0.T_jPjuVpk8OAb4LoNql51CMv1AJAEMcJEN5NddLK9r0"
            )//添加请求头的身份认证Token
            .url(url)
            .post(multipartBody)
            .build()

        var call: Call = httpClient.newCall(request);
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("CreateNftActivityVm", e.toString() + "")
                this@CreateNftActivityVm.cid.postValue("")
            }

            override fun onResponse(call: Call, response: Response) {
                val string = response.body!!.string()
                Log.e("CreateNftActivityVm", string + "")
                try {
                    val objectMapper = ObjectMapper()
                    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    var cidModel = objectMapper.readValue(string, CidModel::class.java)
                    //https://bafybeibhcmesk2rcif6ifnnxiglsd3xdyfmbrkbf5vdqfvdmb3v3lz5osa.ipfs.nftstorage.link/1657679543225252.jpeg
                    if (cidModel.ok) {
                        cidModel.value?.run {
                            var cid = "https://" + cid + ".ipfs.nftstorage.link/" + files[0].name
                            Log.e("CreateNftActivityVm", cid + "")
                            this@CreateNftActivityVm.cid.postValue(cid)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("CreateNftActivityVm", e.toString())
                }

            }
        })
    }

    fun create(
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
        createGift(null,AppData.address,address, cid, lat, lon, name, description,
            num, ranges, sign, numLimit,object : com.topnetwork.net.network.Response.Result<String>() {
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
