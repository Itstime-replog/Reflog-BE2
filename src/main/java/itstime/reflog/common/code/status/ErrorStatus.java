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

	// MyPage 관련 에러
	_MYPAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "MYPAGE404", "해당 마이페이지를 찾을 수 없습니다."),
	_DUPLICATE_NICKNAME(HttpStatus.NOT_FOUND, "MYPAGE409", "이미 존재하는 닉네임입니다."),
	_DUPLICATE_EMAIL(HttpStatus.NOT_FOUND, "MYPAGE409", "이미 존재하는 이메일입니다."),

	// mission 관련 에러
	_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION404", "해당 미션을 찾을 수 없습니다."),

	// badge 관련 에러
	_BADGE_NOT_FOUND(HttpStatus.NOT_FOUND, "BADGE404", "해당 배지를 찾을 수 없습니다."),

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

	//Analysis 관련 에러
	_ANALYSIS_NOT_FOUND(HttpStatus.NOT_FOUND, "ANALYSIS404", "분석보고서를 찾을 수 없습니다."),
	_ANALYSIS_NOT_ALREADY(HttpStatus.NOT_FOUND, "ANALYSIS400", "아직 분석보고서가 생성되지 않았습니다."),

	//S3 관련 에러
	_S3_FILE_OPERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "S3_FILE_500", "S3 파일 작업 중 오류가 발생했습니다."),
	_S3_INVALID_URL(HttpStatus.BAD_REQUEST, "S3_FILE_400", "S3 파일 URL이 잘못되었거나 존재하지 않습니다."),

	// Community 관련 에러
	_COMMUNITY_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMUNITY404", "해당 커뮤니티 게시글을 찾을 수 없습니다."),

	//PostLike 관련 에러
	_POSTLIKE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "POSTLIKE400", "올바른 PostType을 입력해주세요."),
	_POST_NOT_FOUND(HttpStatus.NOT_FOUND, "POSTLIKE404", "해당 게시물이 존재하지 않습니다"),
	// comment 관련 에러
	_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404", "해당 댓글을 찾을 수 없습니다."),
	_PARENT_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404", "해당 부모 댓글을 찾을 수 없습니다."),
	_INVALID_POST_TYPE(HttpStatus.NOT_FOUND, "COMMENT404", "해당 댓글에 대한 게시글의 타입이 존재하지 않습니다."),
	// NotificationSettings 관련 에러
	_NOTIFICATIONSETTINGS_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATIONSETTINGS404", "해당 알림 설정이 존재하지 않습니다"),

	// Notification 관련 에러
	_DELAY(HttpStatus.NOT_FOUND, "NOTIFICATION404", "시작시간이 더 미래일 수는 없습니다."),
	_NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION404", "해당 알림이 존재하지 않습니다")
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
