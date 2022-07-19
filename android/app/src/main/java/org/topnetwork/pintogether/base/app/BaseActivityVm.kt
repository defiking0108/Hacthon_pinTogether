package org.topnetwork.pintogether.base.app

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.topnetwork.pintogether.utils.LogUtils

open class BaseActivityVM : ViewModel() {

    companion object {
        const val TAG = "BaseActivityV3VM"
    }

    /**
     * 管理RxJava请求
     */
    private var compositeDisposable: CompositeDisposable? = null

    // 错误信息
    val showToastMsgLiveData = MutableLiveData<String>()
    val finishCurrentActivityLiveData = MutableLiveData<Boolean>()
    val showProgressHUDField = MutableLiveData<Boolean>() //展示加载中的菊花
    val showCanCancelLoadingField = MutableLiveData<String>() //展示加载中的菊花
    val showViewStatusField = MutableLiveData<Int>()

    open fun afterOnCreate() {

    }

    open fun onStart() {
        LogUtils.dTag("")
    }

    open fun onResume() {}

    open fun onPause() {}

    open fun onStop() {}

    open fun onDestroy() {}

    /**
     * 显示隐藏加载弹窗
     */
    protected fun showLoading() {
        showProgressHUDField.postValue(true)
    }

    /**
     * 隐藏加载窗
     */
    protected fun dismissLoading() {
        showProgressHUDField.postValue(false)
    }

    /**
     * 显示隐藏加载弹窗
     */
    protected fun showCancelLoading(msg: String) {
        showCanCancelLoadingField.postValue(msg)
    }

    /**
     * 隐藏加载窗
     */
    protected fun dismissCancelLoading() {
        showCanCancelLoadingField.postValue("")
    }

    /**
     * 显示错误信息
     *
     * @param msg
     */
    protected fun showToast(msg: String) {
        showToastMsgLiveData.postValue(msg)
    }

    /**
     * 展示无网界面
     */
    protected fun showNoNetView() {
        showViewStatusField.postValue(1)
    }

    /**
     * 展示错误界面
     */
    protected fun showErrorView() {
        showViewStatusField.postValue(2)
    }


    /**
     * 展示空页面
     */
    protected fun showEmptyView() {
        showViewStatusField.postValue(3)
    }

    /**
     * 展示数据界面
     */
    protected fun showContentView() {
        showViewStatusField.postValue(0)
    }

    /**
     * 销毁当前Activity
     *
     * @param msg
     */
    protected fun finishCurrentActivity() {
        finishCurrentActivityLiveData.postValue(true)
    }

    /**
     * 对于Rxjava添加到CompositeDisposable中。
     *
     * @param disposable
     */
    protected fun addDisposable(disposable: Disposable?) {
        if (compositeDisposable == null || compositeDisposable!!.isDisposed) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable!!)
    }

    /**
     * 主动取消请求任务
     */
    protected fun disposeTask() {
        if (compositeDisposable != null && !compositeDisposable!!.isDisposed) {
            compositeDisposable?.dispose()
        }
    }

    override fun onCleared() {
        super.onCleared()
        LogUtils.dTag(TAG, "onCleared")
        if (compositeDisposable != null) {
            LogUtils.dTag(TAG, "onDispose ->Disposable")
            compositeDisposable?.dispose()
            compositeDisposable = null
        }
    }
}