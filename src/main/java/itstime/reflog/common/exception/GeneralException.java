package itstime.reflog.common.exception;

import itstime.reflog.common.code.BaseErrorCode;
import itstime.reflog.common.code.ErrorReasonDTO;
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

	public ErrorReasonDTO getErrorReason(){
		return this.code.getReason();
	}

	public ErrorReasonDTO getErrorReasonHttpStatus(){
		return this.code.getReasonHttpStatus();
	}
}
