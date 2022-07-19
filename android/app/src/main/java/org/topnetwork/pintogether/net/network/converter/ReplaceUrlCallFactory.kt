package com.topnetwork.net.network.converter

import okhttp3.Call
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import org.topnetwork.pintogether.BuildConfig

class ReplaceUrlCallFactory(var delegate: Call.Factory) : Call.Factory {

    companion object {
        const val TAG = "NET_ReplaceUrlCallFactory"
    }

    override fun newCall(request: Request): Call {
            val newHttpUrl = getNewUrl(request)
            if (newHttpUrl != null) {
                val newRequest = request.newBuilder().url(newHttpUrl).build()
                return delegate.newCall(newRequest)
            }
        return delegate.newCall(request)
    }

    private fun getNewUrl(request: Request): HttpUrl? {
        val oldUrl = request.url.toString()
        if(BuildConfig.DEBUG){
            return oldUrl.toHttpUrl()
        }
        else{
//            val newUrl = oldUrl.replace(BuildConfig.NET_BASE_URL, BuildConfig.NET_BASE_URL + "api/")
//            return newUrl.toHttpUrl()
            return oldUrl.toHttpUrl()
        }
    }
}