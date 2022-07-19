package org.topnetwork.pintogether.base.app;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BaseFragmentVM extends ViewModel {

    public MutableLiveData<Boolean> showProgressHUDField = new MutableLiveData<>();//展示加载中的菊花
    public MutableLiveData<String> showToastLiveData = new MutableLiveData<>(); // 错误信息

    /**
     * 管理RxJava请求
     */
    private CompositeDisposable compositeDisposable;

    public void afterOnCreate() {

    }

    protected void addDisposable(Disposable disposable) {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        compositeDisposable.add(disposable);
    }

    /**
     * 显示隐藏加载弹窗
     */
    protected void showLoading() {
        showProgressHUDField.postValue(true);
    }

    /**
     * 隐藏加载窗
     */
    protected void dismissLoading() {
        showProgressHUDField.postValue(false);
    }


    /**
     * 显示错误信息
     *
     * @param msg
     */
    protected void showToast( String msg) {
        showToastLiveData.postValue(msg);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposeObserver();
    }

    /**
     * 取消观察
     */
    protected void disposeObserver() {
        if (compositeDisposable != null) {
            compositeDisposable.dispose();
            compositeDisposable = null;
        }
    }
}
