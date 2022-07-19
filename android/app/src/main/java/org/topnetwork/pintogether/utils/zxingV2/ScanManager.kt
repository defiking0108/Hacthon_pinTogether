package com.topnetwork.zxingV2

import com.topnetwork.zxing.IScanCallBack
import com.topnetwork.zxing.IScanResultCallBack
import java.util.concurrent.ConcurrentHashMap
object ScanManager {

    private val mScanResultCallBackMap = ConcurrentHashMap<String, IScanResultCallBack>()
    private var scanCallBack: IScanCallBack? = null

    /**
     * 注册结果回调接口
     */
    fun registerScanResultCallBack(calssName: String, scanResultCallBack: IScanResultCallBack) {
        if (mScanResultCallBackMap[calssName] != scanResultCallBack) {
            mScanResultCallBackMap[calssName] = scanResultCallBack
        }
    }

    fun unRegisterScanResultCallBack(calssName: String) {
        mScanResultCallBackMap.remove(calssName)
    }

    /**
     * 注册扫描界面控制接口
     */
    fun registerScanCallBack(scanCallBack: IScanCallBack) {
        ScanManager.scanCallBack = scanCallBack
    }

    fun unRegisterScanCallBack() {
        scanCallBack = null
    }

    /**
     * 扫描结果
     */
    fun scanResult(calssName: String, result: String?) {
        mScanResultCallBackMap[calssName]?.result(result)
    }

    /**
     * 关闭扫描界面
     */
    fun finishScanActivity() {
        scanCallBack?.finish()
    }

    /**
     * 处理前
     */
    fun before(msg: String?) {
        scanCallBack?.before(msg)
    }

    /**
     * 处理后
     */
    fun after(msg: String?) {
        scanCallBack?.after(msg)
    }

}