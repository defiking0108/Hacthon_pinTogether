package org.topnetwork.pintogether.utils;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.annotation.SuppressLint;


import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {

    private static String TAG = "RxUtils";
    private static ProgressHUD progressHUD;

    /**
     * 异步任务
     */
    public static <T> void AsyncTask(AsyncTask<T> task) {
        AsyncTask(false, task);
    }

    /**
     * 异步任务
     */
    public static <T> void AsyncTask(boolean showLoading, AsyncTask<T> task) {
        if (task != null) {
            task.onStart();
        }
        Observable.create((ObservableOnSubscribe<T>) e -> {
            if (task == null) {
                e.onError(new IllegalArgumentException("AsyncTask is null"));
            } else {
                e.onNext(task.doAsyncTask());
                e.onComplete();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<T>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (showLoading && ActivityUtils.peek() != null) {
                            progressHUD = ProgressHUD.show(ActivityUtils.peek(), "");
                        }
                    }

                    @Override
                    public void onNext(T t) {
                        LogUtils.dTag("AsyncTask_" + task.taskTag, t);
                        if (progressHUD != null) {
                            progressHUD.dismiss();
                        }
                        task.completed(t);
                    }

                    @Override
                    public void onError(Throwable e) {
                        LogUtils.dTag("AsyncTask_" + task.taskTag, e.getMessage());
                        if (progressHUD != null) {
                            progressHUD.dismiss();
                        }
                        task.failed(e);
                    }

                    @Override
                    public void onComplete() {
                        LogUtils.dTag("AsyncTask_" + task.taskTag, "onComplete");
                    }
                });
    }

    /**
     * 延迟任务
     *
     * @param callback
     */
    public static void delayTask(Callback callback) {
        delayTask(callback, 100);
    }

    /**
     * 延迟任务
     *
     * @param callback
     * @param delay    延迟时间
     */
    @SuppressLint("CheckResult")
    public static void delayTask(Callback callback, long delay) {
        Observable.just(1)
                .delay(delay, MILLISECONDS)
                .compose(SchedulersHandle.io_main())
                .subscribe(s -> {
                    if (callback != null) {
                        callback.doTask();
                    }
                });
    }

    public interface Callback {
        void doTask();
    }

}
