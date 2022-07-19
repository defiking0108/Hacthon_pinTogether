package com.topnetwork.net.network.interceptor
import okhttp3.*
import okio.Buffer
import org.topnetwork.pintogether.BuildConfig
import org.topnetwork.pintogether.utils.LogUtils
import java.io.IOException
internal class LoggingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (BuildConfig.DEBUG) {
            logRequest(request)
        }

        val response = chain.proceed(request)
        if (BuildConfig.DEBUG) {
            return logResponse(response)
        }
        return response
    }

    /**
     * 打印请求信息
     */
    private fun logRequest(request: Request) {
        try {
            LogUtils.dTag(TAG + "_Request", "Sending request url: ${request.url}")
            LogUtils.dTag(TAG + "_Request", "Sending request headers: \n${request.headers}")
            val requestBody = request.body
            if (requestBody != null) {
                val mediaType = requestBody.contentType()
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        LogUtils.dTag(TAG + "_Request", "params : " + bodyToString(request))
                    } else {
                        LogUtils.dTag(TAG + "_Request", "params : " + " maybe [file part] , too large too print , ignored!")
                    }
                }
            }
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e)
        }

    }

    /**
     * 打印响应信息
     */
    private fun logResponse(response: Response): Response {
        try {
            val builder = response.newBuilder()
            val clone = builder.build()
            var body = clone.body
            if (body != null) {
                val mediaType = body.contentType()
                if (mediaType != null) {
                    if (isText(mediaType)) {
                        val resp = body.string()
                        LogUtils.dTag(TAG + "_Response", "==========================start=================================")
                        LogUtils.dTag(TAG + "_Response", "url=" + clone.request.url)
                        LogUtils.json(TAG + "_Response", resp)
                        LogUtils.dTag(TAG + "_Response", "==========================end=================================")
                        body = ResponseBody.create(mediaType, resp)
                        return response.newBuilder().body(body).build()
                    } else {
                        LogUtils.dTag(TAG + "_Response", "data : " + " maybe [file part] , too large too print , ignored!")
                    }
                }
            }
        } catch (e: Exception) {
            LogUtils.eTag(TAG, e)
        }
        return response
    }

    /**
     * 判断是否是文本数据
     */
    private fun isText(mediaType: MediaType?): Boolean {
        return if (mediaType == null) false else "text" == mediaType.subtype
                || "json" == mediaType.subtype
                || "xml" == mediaType.subtype
                || "html" == mediaType.subtype
                || "webviewhtml" == mediaType.subtype
                || "x-www-form-urlencoded" == mediaType.subtype
    }

    /**
     * 转化为字符数据
     */
    private fun bodyToString(request: Request): String {
        try {
            val copy = request.newBuilder().build() ?: return ""
            val buffer = Buffer()
            if (copy.body == null) return ""
            copy.body!!.writeTo(buffer)
            return buffer.readUtf8()
        } catch (e: IOException) {
            return "something error when show requestBody."
        }
    }

    companion object {
        const val TAG = "NET"
    }

}