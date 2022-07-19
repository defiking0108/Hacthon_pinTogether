package com.topnetwork.net.network
import com.topnetwork.net.network.converter.ReplaceUrlCallFactory
import org.topnetwork.pintogether.BuildConfig
import org.topnetwork.pintogether.net.network.converter.JacksonConverterFactory
import org.topnetwork.pintogether.net.network.converter.StringConverterFactory
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory

class RetrofitManager private constructor() {

    private val mRetrofit: Retrofit = Retrofit.Builder()
            .baseUrl(getBaseUrl())
            .callFactory(ReplaceUrlCallFactory(OkHttpManager.instance.getOkHttpClient()))
            .addConverterFactory(StringConverterFactory.create())
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

    /**
     * 获取BaseUrl
     */
    private fun getBaseUrl(): String = BuildConfig.NET_BASE_URL

    /**
     * 创建接口实体类
     */
    fun <T> getApi(clazz: Class<T>): T = mRetrofit.create(clazz)

    companion object {
        @JvmStatic
        val instance: RetrofitManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            RetrofitManager()
        }
    }

}