package com.topnetwork.net.network.interceptor

import okhttp3.*
import okio.Buffer



import java.io.IOException
import java.io.EOFException
import java.nio.charset.Charset
import java.util.*


/**
 * Created by lgc on 2019/6/8.
 * 公共请求头拦截器
 */
class CommonHeadersInterceptor : Interceptor {

    companion object {
        const val TAG = "NET_CommonHeadersInterceptor"
    }
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
                .header("Content-Type", "application/json")
//
//        if(!StringUtils.isEmptyStr(getToken())){
//            builder.header("Authorization",getToken())
//        }
//        //LogUtils.eTag("I18NUtils",I18NUtils.getLanguageDefaultType())
//        builder.header("language", I18NUtils.getLanguageDefaultType())
//        request.header("sign")?.run {
//            // post请求
//            if (TextUtils.equals("POST", request.method())) {
//                if (!StringUtils.isEmpty(accessKey)) {
//                    val requestBody = request.body() as RequestBody
//                    val buffer = Buffer()
//                    requestBody.writeTo(buffer)
//                    var bodyStr = ""
//                    if (isPlaintext(buffer)) {
//                        val charset = Charset.forName("UTF-8")
//                        bodyStr = buffer.readString(charset)
//                        LogUtils.dTag(TAG, "bodyStr >>> $bodyStr")
//                    }
//                    builder.header("token", accessId)
//                        // 签名参数信息
//                        .header("signature", signatureString(accessKey, bodyStr))
//                }
//
//            } else {
//                if (!StringUtils.isEmpty(accessKey)) {
//                    val url = request.url()
//                    try {
//                        var urlStr = url.toString()
//                        val urls = urlStr.split("?")
//                        if (urls.isNotEmpty()) {
//                            urlStr = "${urls[0]}?"
//                        }
//                        LogUtils.dTag(LoggingInterceptor.TAG + "GET_Request", "Sending request url: $url")
//                        LogUtils.dTag(LoggingInterceptor.TAG + "GET_Request", "Sending request url: $urlStr")
//                        val queryParameterNames = request.url().queryParameterNames()
//                        val params: SortedMap<String, String> = TreeMap()
//                        for (key in queryParameterNames) {
//                            params[key] = url.queryParameter(key)
//                        }
//                        val paramsStr = signatureParams(params, accessKey, accessId)
//                        val newUrl = urlStr + paramsStr
//                        LogUtils.dTag(LoggingInterceptor.TAG + "GET_Request", "Sending request new url: $newUrl")
//                        builder.url(newUrl)
//                    } catch (e: Exception) {
//                        builder.url(url)
//                    }
//                }
//
//            }
//        }
        return chain.proceed(builder.build())
    }

    /**
     * 判断是否是字符
     */
    private fun isPlaintext(buffer: Buffer): Boolean {
        try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            return true
        } catch (e: EOFException) {
            return false
        }
    }

}
