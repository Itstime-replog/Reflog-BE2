package itstime.reflog.common.exception;

import itstime.reflog.common.code.BaseErrorCode;
import itstime.reflog.common.code.dto.ErrorReasonDto;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

	private BaseErrorCode code;

	public GeneralException(String message) {
		super(message);
		this.code = null;
	}

	public GeneralException(BaseErrorCode code) {
		super(code.getReason().getMessage());
		this.code = code;
	}

	public ErrorReasonDto getErrorReason(){
		return this.code.getReason();
	}

	public ErrorReasonDto getErrorReasonHttpStatus(){
		return this.code.getReasonHttpStatus();
	}
}
