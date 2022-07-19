package com.topnetwork.net.model

import com.fasterxml.jackson.annotation.JsonProperty

class ApiResponse<T> {

    var code: Int = 0 // 信息编号
    @JsonProperty("logno")
    var logNo: String = ""  // 日志编号
    var message: String = "" // 提示信息
    var name: String = "" // 应用程序名称
    @JsonProperty("servertime")
    var serverTime: String = "" // 服务器时间
    var result: T? = null // 响应内容，只有在信息编号为200的时候返回
    var pageInfo: PageInfo? = null // 分页信息，只有在信息编号为200及分页查询接口中会返回

    constructor()

    override fun toString(): String {
        return "ApiResponse(code=$code, logNo='$logNo', message='$message', name='$name', serverTime='$serverTime', result=$result, pageInfo=$pageInfo)"
    }


}

// 分页加载数据
class PageInfo {
    val totalCount: Int = 0 // 总记录数

    override fun toString(): String {
        return "PageInfo(totalCount=$totalCount)"
    }

}
