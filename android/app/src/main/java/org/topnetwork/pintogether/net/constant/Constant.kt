package com.topnetwork.net.constant

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull

// 列表数据每页请求数量
const val REQUEST_PAGE_SIZE = 20
const val PLATFORM_TYPE_ANDROID = 2  // 平台类型1、ios；2、安卓

@JvmField
val MEDIA_TYPE_JSON = "application/json; charset=UTF-8".toMediaTypeOrNull()

@JvmField
val FORM_MULTIPART_TYPE = "multipart/form-data".toMediaTypeOrNull()
