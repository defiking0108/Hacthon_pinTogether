package org.topnetwork.pintogether.net.network.exception;


import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.utils.StringUtils;

/**
 * Created by lgc on 2019/6/8.
 */
public enum ApiExceptionEnum {

    /**
     * common exceptions
     */
    API_EXCEPTION_DEFAULT(0, StringUtils.getString(R.string.str_exception_default)
            , StringUtils.getString(R.string.str_exception_default)),
    API_DATA_EMPTY(1, StringUtils.getString(R.string.str_data_empty)
            , StringUtils.getString(R.string.str_data_empty)),
    API_TIME_OUT_EXCEPTION(2, StringUtils.getString(R.string.str_time_out_exception)
            , StringUtils.getString(R.string.str_time_out_exception)),
    API_CONNECT_EXCEPTION(3, StringUtils.getString(R.string.str_connect_exception)
            , StringUtils.getString(R.string.str_connect_exception)),
    API_UNKNOWN_HOST_EXCEPTION(4, StringUtils.getString(R.string.str_unknown_host_exception)
            , StringUtils.getString(R.string.str_unknown_host_exception)),
    API_UNKNOWN_SERVICE_EXCEPTION(5, StringUtils.getString(R.string.str_unknown_service_exception)
            , StringUtils.getString(R.string.str_unknown_service_exception)),
    API_IO_EXCEPTION(6, StringUtils.getString(R.string.str_io_exception)
            , StringUtils.getString(R.string.str_io_exception)),
    API_NETWORK_ON_MAIN_THREAD_EXCEPTION(7, StringUtils.getString(R.string.str_network_on_main_thread_exception)
            , StringUtils.getString(R.string.str_network_on_main_thread_exception)),
    API_RUNTIME_EXCEPTION(8, StringUtils.getString(R.string.str_runtime_exception)
            , StringUtils.getString(R.string.str_runtime_exception)),
    API_HTTP_EXCEPTION(9, StringUtils.getString(R.string.str_http_exception)
            , StringUtils.getString(R.string.str_http_exception)),
    API_PARSE_EXCEPTION(10, StringUtils.getString(R.string.str_parse_exception)
            , StringUtils.getString(R.string.str_parse_exception));


    /**
     * 异常code
     */
    private int errorCode;

    /**
     * 异常信息
     */
    private String errorMsg;

    /**
     * 异常描述
     */
    private String desc;

    ApiExceptionEnum(int errorCode, String errorMsg, String desc) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.desc = desc;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getDesc() {
        return desc;
    }
}
