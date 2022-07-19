package org.topnetwork.pintogether.net.network

import com.topnetwork.net.constant.MEDIA_TYPE_JSON
import com.topnetwork.net.network.TAG
import okhttp3.RequestBody
import org.topnetwork.pintogether.net.ObjectMapperUtils
import org.topnetwork.pintogether.utils.LogUtils
import java.util.*
import kotlin.collections.HashMap
class RequestParamNoKey {

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

        fun build(): RequestParamNoKey {
            return RequestParamNoKey(this)
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

}
