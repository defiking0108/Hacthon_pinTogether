package com.topnetwork.zxing
// 回调结果
interface IScanResultCallBack {

    /**
     * 扫描结果
     */
    fun result(result: String?)


}

// 扫描源头
interface IScanCallBack {

    /**
     * 扫描结果处理前
     */
    fun before(msg: String?)

    /**
     * 扫描结果处理后
     */
    fun after(msg: String?)

    /**
     * 结束
     */
    fun finish()


}
