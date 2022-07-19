package org.topnetwork.pintogether.net.network.interceptor;

import org.topnetwork.pintogether.net.network.util.NetworkUtils;
import org.topnetwork.pintogether.utils.LogUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CacheInterceptor implements Interceptor {

    private static final String TAG = "NET_CacheInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        if (!NetworkUtils.isAvailable()) {
            request = request.newBuilder()
                    .cacheControl(CacheControl.FORCE_CACHE)
                    .build();
            LogUtils.eTag(TAG, "no network");
        }

        Response response = chain.proceed(request);
        if (NetworkUtils.isAvailable()) {
            int maxAge = 0 * 60; // 有网络时 设置缓存超时时间为0;
            response.newBuilder()
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .removeHeader("Pragma")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                    .build();
        } else {
            int maxStale = 60 * 60 * 24 * 7; // 无网络时，设置超时为1天
            LogUtils.eTag(TAG, "has maxStale=" + maxStale);
            response.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .removeHeader("Pragma")
                    .build();
        }
        return response;
    }

}
