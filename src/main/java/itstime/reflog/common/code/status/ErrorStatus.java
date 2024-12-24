package itstime.reflog.common.code.status;

import org.springframework.http.HttpStatus;

import itstime.reflog.common.code.BaseErrorCode;
import itstime.reflog.common.code.dto.ErrorReasonDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

	// 가장 일반적인 응답
	_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
	_BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
	_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
	_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

	// 멤버 관련 에러
	_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER404", "해당 회원을 찾을 수 없습니다."),
	_DUPLICATE_MEMBER(HttpStatus.CONFLICT, "MEMBER409", "이미 존재하는 회원입니다."),
	_INVALID_MEMBER_CREDENTIALS(HttpStatus.UNAUTHORIZED, "MEMBER401", "잘못된 인증 정보입니다."),
	_MEMBER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "MEMBER403", "해당 회원은 권한이 없습니다."),

	// TodoList 관련 에러
	_TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO404", "해당 투두리스트를 찾을 수 없습니다."),
	_TODO_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "TODO403", "투두리스트를 수정할 권한이 없습니다."),

	//DailyGoal 관련 에러
	_DAILY_GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "GOAL404", "오늘의 목표를 찾을 수 없습니다."),
	_DAILY_GOAL_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "GOAL403", "오늘의 목표를 수정할 권한이 없습니다."),
	_DAILY_GOAL_ALREADY_EXISTS(HttpStatus.CONFLICT, "GOAL409", "이미 해당 날짜에 학습 목표가 존재합니다."),

	// TodoList 관련 에러
	_SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "TODO404", "해당 일정등록을 찾을 수 없습니다."),
	_SCHEDULE_UPDATE_NOT_ALLOWED(HttpStatus.FORBIDDEN, "TODO403", "일정등록을 수정할 권한이 없습니다."),

	// Retrospect 관련 에러
	_RETROSPECT_NOT_FOUND(HttpStatus.NOT_FOUND, "RETROSPECT404", "해당 회고일지를 찾을 수 없습니다."),

	_ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS404", "분석보고서를 찾을 수 없습니다."),
	_ANALYSIS_NOT_ALREADY(HttpStatus.NOT_FOUND, "ANALYSIS400", "아직 분석보고서가 생성되지 않았습니다.");
			;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

	@Override
	public ErrorReasonDto getReason() {
		return ErrorReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.build();
	}

	@Override
	public ErrorReasonDto getReasonHttpStatus() {
		return ErrorReasonDto.builder()
			.message(message)
			.code(code)
			.isSuccess(false)
			.httpStatus(httpStatus)
			.build()
			;
	}
}
