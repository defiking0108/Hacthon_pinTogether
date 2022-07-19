package org.topnetwork.pintogether.utils;

public abstract class AsyncTask<T> {

    // 任务标志
    public String taskTag;

    public AsyncTask() {
        this("");
    }

    public AsyncTask(String taskTag) {
        this.taskTag = taskTag;
    }

    /**
     * 开始
     */
    public void onStart(){

    }

    /**
     * 任务
     *
     * @return
     */
    public abstract T doAsyncTask();

    /**
     * 完成
     *
     * @param t
     */
    public abstract void completed(T t);

    /**
     * 失败
     */
    public void failed(Throwable e) {
    }

}
