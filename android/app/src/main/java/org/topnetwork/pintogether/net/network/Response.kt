package com.topnetwork.net.network

import com.topnetwork.net.model.ApiResponse
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus
import org.topnetwork.pintogether.net.network.exception.ApiExceptionUtil
import org.topnetwork.pintogether.utils.LogUtils
import retrofit2.HttpException

class Response {

    companion object {
        const val TAG = "NET_Response"
        const val CODE_RESPONSE_TIMEOUT = 2  // 连接超时
        const val CODE_NETWORK_IS_NOT_STABLE = 3 // 网络异常
        const val CODE_NO_NETWORK = 6  // 网络异常
        const val API_HTTP_EXCEPTION = 9  // 网络异常
        const val RESULT_SUCCESS_START = 200 // 请求成功
        const val RESULT_SUCCESS_NO_DATA = 204 // 请求成功 无数据
        const val RESULT_SUCCESS_END = 300 // 请求成功
        const val RESULT_SYSTEM_BUSY = 505 // 系统忙,请稍候再试
        const val RESULT_NO_MESSAGE = 1017 // 暂无记录
        const val RESULT_REQUEST_FAST = 11001 // 操作过快，休息一下
        const val RESULT_NO_COIN_INFO = 12001 // 币信息不存在
        const val RESULT_NO_INFO = 10006 // 登录信息不存在
        const val RESULT_NO_LOGIN = 10007 // 未登录
        const val RESULT_NO_DATA = 0 // 请求成功 但数据为空
    }

    /**
     * 获取字符串结果的Observer
     */
    open class StringResult<T> : Observer<T> {

        var isShowLoading = false

        @JvmOverloads
        constructor(showLoading: Boolean = false) {
            isShowLoading = showLoading
        }

        override fun onComplete() {
        }

        override fun onSubscribe(d: Disposable) {
        }

        override fun onNext(result: T) {
        }

        override fun onError(e: Throwable) {
        }

    }

    /**
     * 数据结果
     */
    open class Result<T> {

        var isShowLoading = false

        @JvmOverloads
        constructor(showLoading: Boolean = false) {
            isShowLoading = showLoading
        }

        open fun onStart(disposable: Disposable?) {
        }

        open fun onFinished(disposable: Disposable?) {
        }

        open fun succeeded(pageSize: Int, result: T) {
            succeeded(result)
        }

        open fun succeeded(result: T) {

        }

        open fun succeeded() {

        }

        open fun failed(code: Int, message: String?) {
            failed(message)
        }

        open fun noNet(code: Int, message: String?) {

        }

        open fun failed(message: String?) {

        }

        // 登录失效
        open fun noLogin(code: Int) {

        }

    }

    // 请求结果数据处理
    class ModelResult<T>(result: Result<T>) : Observer<ApiResponse<T>> {

        private var disposable: Disposable? = null
        private var mResult: Result<T> = result

        override fun onComplete() {
            mResult.onFinished(disposable)
        }

        override fun onSubscribe(d: Disposable) {
            disposable = d
            mResult.onStart(disposable)
        }

        override fun onNext(response: ApiResponse<T>) {
            LogUtils.dTag(TAG, response)
            when (response.code) {
                RESULT_SUCCESS_NO_DATA -> {
                    mResult.succeeded()
                }
                in RESULT_SUCCESS_START until RESULT_SUCCESS_END -> {
                    if (response.pageInfo != null && response.result != null) {
                        mResult.succeeded(response.pageInfo!!.totalCount, response.result!!)
                    } else if (response.result != null) {
                        mResult.succeeded(response.result!!)
                    } else {
                        mResult.succeeded()
                    }
                }
                RESULT_NO_COIN_INFO -> {
                    mResult.failed(response.code, response.message)
                }

//                RESULT_NO_LOGIN,RESULT_NO_INFO -> {
//                    mResult.noLogin(response.code)
//                    EventBus.getDefault().post(LoginFailureEvent())
//                }

                else -> {
                    mResult.failed(response.code, response.message)
                }
            }
        }

        override fun onError(e: Throwable) {
            LogUtils.eTag(TAG,e.message)
            val apiException = ApiExceptionUtil.onError(e)
            LogUtils.dTag(TAG, apiException)
            when (apiException.code) {
                // 数字对应值在ApiException中查看
                CODE_RESPONSE_TIMEOUT,
                API_HTTP_EXCEPTION,
                CODE_NETWORK_IS_NOT_STABLE,
                CODE_NO_NETWORK -> {
                    if(apiException.code == API_HTTP_EXCEPTION){
                        val expectation = e as HttpException
                        val code = expectation.code()
                        if(code == 401){
                            //EventBus.getDefault().post(LoginFailureEvent())
                        }else{
                            mResult.noNet(apiException.code, apiException.message)
                        }
                    }else{
                        mResult.noNet(apiException.code, apiException.message)
                    }
                    //ToastUtils.showShort(apiException.message)
                }
                else -> {
                    LogUtils.eTag(TAG, apiException.message)
                }
            }
            mResult.failed(apiException.code, apiException.message)
            mResult.onFinished(disposable)
        }

    }

}