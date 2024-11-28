package itstime.reflog.common.code.dto;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorReasonDto {
	private String message;
	private String code;
	private Boolean isSuccess;
	private HttpStatus httpStatus;
}
