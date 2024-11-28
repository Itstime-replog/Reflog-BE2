package itstime.reflog.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import itstime.reflog.common.code.BaseCode;
import itstime.reflog.common.code.BaseErrorCode;
import itstime.reflog.common.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class ApiResponse<T> {

	@JsonProperty("isSuccess")
	private final Boolean isSuccess;
	private final String code;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	// 성공한 경우 응답 생성
	public static <T> ResponseEntity<ApiResponse<T>> onSuccess(BaseCode code, T payload) {
		ApiResponse<T> response = new ApiResponse<>(true, code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(), payload);
		return ResponseEntity.status(code.getReasonHttpStatus().getHttpStatus()).body(response);
	}

	public static <T> ResponseEntity<ApiResponse<T>> onSuccess(BaseCode code) {
		ApiResponse<T> response = new ApiResponse<>(true, code.getReasonHttpStatus().getCode(), code.getReasonHttpStatus().getMessage(), null);
		return ResponseEntity.status(code.getReasonHttpStatus().getHttpStatus()).body(response);
	}

	// 실패한 경우 응답 생성
	public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
		return new ApiResponse<>(false, code, message, data);
	}
}
