package org.topnetwork.pintogether.utils;

import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

//线程调度类
public class SchedulersHandle {
    public interface CountDownCallBack {
        void onNext(long time);

        void onComplete();
    }

    /**
     * @author lgc
     * @desc 单纯线程调度
     */
    public static <T> ObservableTransformer<T, T> io_main() {
        return upstream -> upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @author lgc
     * @desc 线程调度+绑定生命周期
     */
    public static <T> ObservableTransformer<T, T> io_main(final LifecycleProvider activityOrFragment) {
        return upstream -> upstream
                .compose(activityOrFragment.<T>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * @author lgc
     * @desc 线程耗时操作并之后回到主线程
     */
    public static void doIOtoMain(Consumer<Object> io, Consumer<Object> main) {
        Observable.just(1).doOnSubscribe(io).compose(io_main()).subscribe(main);
    }

    /**
     * @param activityOrFragment 页面销毁时取消倒计时
     * @param seconds            秒倒计时的总时长
     * @param countDownCallBack  回调
     * @author lgc
     * @desc 倒计时
     */
    public static void countDownAction(final LifecycleProvider activityOrFragment, long seconds, CountDownCallBack countDownCallBack) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(seconds)
                .map(aLong -> seconds - aLong)
                .compose(io_main(activityOrFragment))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Long aLong) {
                        countDownCallBack.onNext(aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        countDownCallBack.onComplete();
                    }
                });
    }

    private Disposable disposable;

    public void cancel() {
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
    }

    /**
     * lgc
     * 倒计时 需要刷新的时候使用
     */
    public void countDown(final LifecycleProvider activityOrFragment, long seconds, CountDownCallBack countDownCallBack) {
        Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(seconds)
                .map(aLong -> seconds - aLong)
                .compose(io_main(activityOrFragment))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        countDownCallBack.onNext(aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        countDownCallBack.onComplete();
                    }
                });
    }
}
