package itstime.reflog.todolist.controller;

import java.time.LocalDate;
import java.util.List;

import itstime.reflog.common.annotation.UserId;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.todolist.dto.TodolistDto;
import itstime.reflog.todolist.service.TodolistService;
import jakarta.validation.Valid;
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
	public ResponseEntity<CommonApiResponse<Void>> createTodolist(
			@UserId String memberId,
			@RequestBody TodolistDto.TodolistSaveOrUpdateRequest dto
	) {
		todolistService.createTodolist(memberId, dto);
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
	public ResponseEntity<CommonApiResponse<List<TodolistDto.TodolistResponse>>> getTodolistByMemberIdAndDate(
			@UserId String memberId,
			@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		List<TodolistDto.TodolistResponse> todolists = todolistService.getTodolistByMemberIdAndDate(memberId, date);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(todolists));
	}

	@Operation(
		summary = "투두리스트 수정 API",
		description = "투두리스트 항목의 일부 정보를 수정합니다. AccessToken 필요.",
		parameters = {
			@Parameter(
				name = "todolistId",
				description = "수정하려는 투두리스트의 고유 ID",
				required = true
			)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "투두리스트 수정 성공",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "해당 투두리스트를 찾을 수 없음",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			),
			@ApiResponse(
				responseCode = "500",
				description = "서버 에러",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			)
		}
	)
	@PatchMapping("/todolist/{todolistId}")
	public ResponseEntity<CommonApiResponse<Void>> updateTodolist(
		@PathVariable Long todolistId,
		@UserId String memberId,
		@RequestBody @Valid TodolistDto.TodolistSaveOrUpdateRequest request
	) {
		todolistService.updateTodolist(todolistId, memberId, request);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

	@Operation(
		summary = "투두리스트 삭제 API",
		description = "특정 투두리스트 항목을 삭제합니다. AccessToken 필요.",
		parameters = {
			@Parameter(
				name = "todolistId",
				description = "삭제하려는 투두리스트의 고유 ID",
				required = true
			)
		},
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "투두리스트 삭제 성공",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			),
			@ApiResponse(
				responseCode = "404",
				description = "해당 투두리스트를 찾을 수 없음",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			),
			@ApiResponse(
				responseCode = "500",
				description = "서버 에러",
				content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
			)
		}
	)
	@DeleteMapping("/todolist/{todolistId}")
	public ResponseEntity<CommonApiResponse<Void>> deleteTodolist(@PathVariable("todolistId") Long todolistId) {
		todolistService.deleteTodolist(todolistId);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

	@Operation(
			summary = "투두리스트 체크 API",
			description = "특정 투두리스트 항목을 삭제합니다. AccessToken 필요.",
			parameters = {
					@Parameter(
							name = "todolistId",
							description = "삭제하려는 투두리스트의 고유 ID",
							required = true
					)
			},
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "투두리스트 삭제 성공",
							content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
					),
					@ApiResponse(
							responseCode = "404",
							description = "해당 투두리스트를 찾을 수 없음",
							content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
					),
					@ApiResponse(
							responseCode = "500",
							description = "서버 에러",
							content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
					)
			}
	)
	@PostMapping("/todolist/check")
	public ResponseEntity<CommonApiResponse<Void>> checkTodolist(
			@UserId String memberId
	) {
		todolistService.checkTodolist(memberId);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}
}
