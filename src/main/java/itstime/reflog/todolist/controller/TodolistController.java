package itstime.reflog.todolist.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.common.code.ErrorReasonDTO;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.todolist.dto.TodolistDTO;
import itstime.reflog.todolist.service.TodolistService;
import lombok.RequiredArgsConstructor;

@Tag(name = "TODOLIST API", description = "투두리스트에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/todo")
public class TodolistController {
	private final TodolistService todolistService;

	@Operation(
		summary = "투두리스트 생성 API",
		description = "새로운 투두리스트 항목을 생성합니다. AccessToken 필요.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "투두리스트 생성 성공"
			),
			@ApiResponse(
				responseCode = "404",
				description = "해당 회원을 찾을 수 없음"
			),
			@ApiResponse(
				responseCode = "500",
				description = "서버 에러"
			)
		}
	)
	@PostMapping("/todolist")
	public ResponseEntity<CommonApiResponse<Void>> createTodolist(@RequestBody TodolistDTO.TodolistSaveOrUpdateRequest dto) {
		todolistService.createTodolist(dto);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

	@Operation(
		summary = "투두리스트 조회 API",
		description = "특정 회원의 특정 날짜에 해당하는 투두리스트를 조회합니다.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "투두리스트 조회 성공",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "해당 회원 또는 투두리스트를 찾을 수 없음",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			),
			@ApiResponse(
				responseCode = "500",
				description = "서버 에러",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			)
		}
	)
	@GetMapping("/todolist")
	public ResponseEntity<CommonApiResponse<List<TodolistDTO.TodolistResponse>>> getTodolistByMemberIdAndDate(
		@RequestParam Long memberId,
		@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		List<TodolistDTO.TodolistResponse> todolists = todolistService.getTodolistByMemberIdAndDate(memberId, date);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(todolists));
	}




}
