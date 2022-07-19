package org.topnetwork.pintogether.net.network.exception;
import org.topnetwork.pintogether.R;
import org.topnetwork.pintogether.utils.StringUtils;

/**
 * Created by lgc on 2019/6/8.
 *
 */
public class ApiException extends RuntimeException {

	private int code;  // 异常码
	private String message;  // 异常信息

	public ApiException(String message) {
		super(message);
		this.message = message;
	}

	public ApiException(ApiExceptionEnum apiExceptionEnum) {
		this(apiExceptionEnum.getErrorCode(), StringUtils.getString(R.string.str_exception_default));
	}

	public ApiException(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public int getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "ApiException{" +
				"code=" + code +
				", message='" + message + '\'' +
				'}';
	}

}