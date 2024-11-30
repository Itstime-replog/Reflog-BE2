package itstime.reflog.common.code.status;

import org.springframework.http.HttpStatus;

import itstime.reflog.common.code.BaseCode;
import itstime.reflog.common.code.dto.ReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SuccessStatus implements BaseCode {
	//가장 일반적인 응답
	_OK(HttpStatus.OK, "COMMON200", "성공입니다."),

	_CREATED_ACCESS_TOKEN(HttpStatus.CREATED, "201", "액세스 토큰 재발행에 성공했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ReasonDto getReason(){
		return ReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.build()
			;
	}

	@Override
	public ReasonDto getReasonHttpStatus(){
		return ReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(true)
			.httpStatus(httpStatus)
			.build()
			;
	}

}
