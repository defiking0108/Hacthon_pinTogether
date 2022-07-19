package com.topnetwork.net.network

import com.topnetwork.net.network.interceptor.CommonHeadersInterceptor
import com.topnetwork.net.network.interceptor.LoggingInterceptor
import okhttp3.OkHttpClient
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
class OkHttpManager private constructor() {

    private val mOkHttpClient: OkHttpClient

    init {

        val trustManager = object : X509TrustManager {
            override fun checkClientTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun checkServerTrusted(p0: Array<out X509Certificate>?, p1: String?) {

            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
        val trustAllCerts = arrayOf<TrustManager>(trustManager)
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())

        val okHttpBuilder = OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .sslSocketFactory(sslContext.socketFactory, trustManager)
                .hostnameVerifier { s, sslSession -> true }
                .connectTimeout(HTTP_CONNECT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_READ_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRIGHT_TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .addInterceptor(CommonHeadersInterceptor())
                .addInterceptor(LoggingInterceptor())
        //.addInterceptor(CacheInterceptor())
        mOkHttpClient = okHttpBuilder.build()
    }

    fun getOkHttpClient(): OkHttpClient = mOkHttpClient

    companion object {

        const val HTTP_CONNECT_TIMEOUT: Int = 30 * 1000
        const val HTTP_READ_TIMEOUT: Int = 30 * 1000
        const val HTTP_WRIGHT_TIMEOUT: Int = 30 * 1000

        @JvmStatic
        val instance: OkHttpManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            OkHttpManager()
        }
    }

}