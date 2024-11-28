package itstime.reflog.common.code;

import itstime.reflog.common.code.dto.ErrorReasonDto;

public interface BaseErrorCode {

	public ErrorReasonDto getReason();

	public ErrorReasonDto getReasonHttpStatus();
}
