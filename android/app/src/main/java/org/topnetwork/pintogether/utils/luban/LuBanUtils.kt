package org.topnetwork.pintogether.utils.luban
import android.os.Environment
import java.io.File

fun getPath(): String {
    val path: String = Environment.getExternalStorageDirectory().toString() + "/Luban/image/"
    val file = File(path)
    return if (file.mkdirs()) {
        path
    } else path
}
