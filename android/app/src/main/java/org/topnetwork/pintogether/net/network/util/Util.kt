package com.topnetwork.net.network.util

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.topnetwork.net.network.OkHttpManager
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import org.topnetwork.pintogether.base.NormalBaseConfig
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream

/**
 * 下载图片
 */
fun loadPic(url: String, destFileDir: String, destFileName: String): Observable<String> {
    return Observable.create<String> {
        try {
            val request = Request.Builder()
                    .url(url)
                    .build()

            //储存下载文件的目录
            val dir = File(destFileDir)
            if (!dir.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, destFileName)
            val response = OkHttpManager.instance.getOkHttpClient().newCall(request).execute()
            response.body!!.byteStream()
            val inputStream = response.body!!.byteStream()
            val bis = BufferedInputStream(inputStream)
//            var total = response.body()!!.contentLength()
            val fos = FileOutputStream(file)
            val buf = ByteArray(2048)
            var len = 0
            var sum: Long = 0
            while (inputStream.read(buf).also { len = it } != -1) {
                fos.write(buf, 0, len)
                sum += len.toLong()
//                val progress = (sum * 1.0f / total * 100).toInt()
                //下载中更新进度条
            }
            fos.flush()
            fos.close()
            bis.close()
            inputStream.close()

            //保存图片后发送广播通知更新数据库
            val uri = if (Build.VERSION.SDK_INT >= 24) {
                FileProvider.getUriForFile(
                    NormalBaseConfig.getContext(),
                        NormalBaseConfig.getContext().packageName + ".fileprovider", file)
            } else {
                Uri.fromFile(file)
            }
            NormalBaseConfig.getContext().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            it.onNext(file.absolutePath)
        } catch (e: Exception) {
            it.onError(e)
        }
    }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}