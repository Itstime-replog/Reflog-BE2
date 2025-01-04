package itstime.reflog.schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.schedule.dto.ScheduleDto;
import itstime.reflog.schedule.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "SCHEDULE API", description = "일정등록에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(
            summary = "일정등록 생성 API",
            description = "새로운 일정등록 항목을 생성합니다. AccessToken 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정등록 생성 성공"
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
    @PostMapping("/schedule")
    public ResponseEntity<CommonApiResponse<Void>> createSchedule(
//            @RequestParam Long memberId,
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody ScheduleDto.ScheduleSaveOrUpdateRequest dto
    ) {
        scheduleService.createSchedule(authorizationHeader, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "일정등록 조회 API",
            description = "특정 회원의 특정 날짜에 해당하는 일정등록을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정등록 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 일정등록을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<CommonApiResponse<ScheduleDto.ScheduleResponse>> getSchedule(
            @PathVariable Long scheduleId,
            @RequestParam Long memberId
    ) {
        ScheduleDto.ScheduleResponse schedule = scheduleService.getSchedule(scheduleId, memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(schedule));
    }

    @Operation(
            summary = "일정등록 전체조회 API",
            description = "특정 회원의 일정등록을 전체조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정등록 전체조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 일정등록을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @GetMapping("/schedule")
    public ResponseEntity<CommonApiResponse<List<ScheduleDto.ScheduleAllResponse>>> getAllSchedule(
            @RequestParam Long memberId,
            @RequestParam int month
    ) {
        List<ScheduleDto.ScheduleAllResponse> schedules = scheduleService.getAllSchedule(memberId, month);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(schedules));
    }



    @Operation(
            summary = "일정등록 수정 API",
            description = "일정등록 항목의 일부 정보를 수정합니다. AccessToken 필요.",
            parameters = {
                    @Parameter(
                            name = "scheduleId",
                            description = "수정하려는 일정등록의 고유 ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정등록 수정 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 일정등록을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @PatchMapping("/schedule/{scheduleId}")
    public ResponseEntity<CommonApiResponse<Void>> updateSchedule(
            @PathVariable Long scheduleId,
            @RequestParam Long memberId,
            @RequestBody @Valid ScheduleDto.ScheduleSaveOrUpdateRequest request
    ) {
        scheduleService.updateSchedule(scheduleId, memberId, request);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "일정등록 삭제 API",
            description = "특정 일정등록 항목을 삭제합니다. AccessToken 필요.",
            parameters = {
                    @Parameter(
                            name = "scheduleId",
                            description = "삭제하려는 일정등록의 고유 ID",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "일정등록 삭제 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 일정등록을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<CommonApiResponse<Void>> deleteSchedule(
            @PathVariable("scheduleId") Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }
}
