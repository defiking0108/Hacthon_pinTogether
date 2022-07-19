package com.topnetwork.zxingV2

import android.content.Intent
import org.topnetwork.pintogether.utils.ActivityUtils
import org.topnetwork.pintogether.utils.zxing.decoding.Intents
import org.topnetwork.pintogether.utils.zxingV2.CaptureActivity

fun startCaptureActivity(className: String, requestCode: Int) {
    val intent = Intent().apply {
        action = Intents.Scan.ACTION
        putExtra(Intents.Scan.SCAN_FORMATS, "QR_CODE")
        putExtra(CaptureActivity.SCAN_KEY, className)
        ActivityUtils.push(CaptureActivity::class.java, this, requestCode)
    }
}