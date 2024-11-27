package itstime.reflog.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import itstime.reflog.common.code.BaseCode;
import itstime.reflog.common.code.status.SuccessStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class CommonApiResponse<T> {

	@JsonProperty("isSuccess")
	private final Boolean isSuccess;
	private final String code;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private T result;

	// 성공한 경우 응답 생성
	public static <T> CommonApiResponse<T> onSuccess(T result){
		return new CommonApiResponse<>(true, SuccessStatus._OK.getCode() , SuccessStatus._OK.getMessage(), result);
	}

	public static <T> CommonApiResponse<T> of(BaseCode code, T result){
		return new CommonApiResponse<>(true, code.getReasonHttpStatus().getCode() , code.getReasonHttpStatus().getMessage(), result);
	}

	// 실패한 경우 응답 생성
	public static <T> CommonApiResponse<T> onFailure(String code, String message, T data) {
		return new CommonApiResponse<>(false, code, message, data);
	}
}
