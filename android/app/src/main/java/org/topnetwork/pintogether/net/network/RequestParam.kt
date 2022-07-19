package com.topnetwork.net.network

import com.topnetwork.net.constant.MEDIA_TYPE_JSON
import okhttp3.RequestBody
import org.topnetwork.pintogether.net.ObjectMapperUtils
import org.topnetwork.pintogether.utils.LogUtils
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
class RequestParam {

    /**
     * 创建Map
     * @return
     */
    val params: HashMap<String, Any>

    constructor(builder: Builder) {
        params = builder.params
    }

    /**
     * 创建RequestBody
     * @return
     */
    val requestBody: RequestBody
        get() = RequestBody.create(MEDIA_TYPE_JSON, toJson(params))

    val requestBodyNoEncode: RequestBody
        get() = RequestBody.create(MEDIA_TYPE_JSON, toJson(params))

    fun requestBodyNoEncode(array:ArrayList<String>):RequestBody{
        return RequestBody.create(MEDIA_TYPE_JSON, toJson(array))
    }

    class Builder {

        val params: HashMap<String, Any> = HashMap()

        fun put(params: TreeMap<String, String>): Builder {
            params.putAll(params)
            return this
        }

        fun put(key: String?, value: String?): Builder {
            if (key == null || value == null) return this
            params[key] = value
            return this
        }

        fun put(key: String?, value: Number?): Builder {
            if (key == null || value == null) return this
            params[key] = value
            return this
        }

        fun put(key: String?, value: Array<String>?): Builder {
            if (key == null || value == null) return this
            params[key] = value
            return this
        }

        fun put(key: String?, value: List<String>?): Builder {
            if (key == null || value == null) return this
            params[key] = value
            return this
        }

        fun put(key: String?, value: Any?): Builder {
            if (key == null || value == null) return this
            params[key] = value
            return this
        }

        fun build(): RequestParam {
            return RequestParam(this)
        }

    }

    /**
     * 生成Json
     * @param params 参数Map
     */
    private fun toJson(params: Map<String, Any>): String {
        val jsonStr = ObjectMapperUtils.getObjectMapper().writeValueAsString(params)
        LogUtils.dTag(TAG, "jsonStr >>> $jsonStr")
        return jsonStr
    }

    /**
     * 生成Json
     * @param params 参数Map
     */
    private fun toJson(params: Any): String {
        val jsonStr = ObjectMapperUtils.getObjectMapper().writeValueAsString(params)
        LogUtils.dTag(TAG, "jsonStr >>> $jsonStr")
        return jsonStr
    }

//    /**
//     * Base64对json数据进行加密
//     * @param jsonByteArray json数据的字节数组
//     */
//    private fun encodeBase64(jsonByteArray: ByteArray): String {
//        val bodyStr = String(Base64.encodeBase64(jsonByteArray))
//        LogUtils.dTag(TAG, "bodyStr >>> $bodyStr")
//        return bodyStr
//    }

}
