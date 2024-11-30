package itstime.reflog.goal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.goal.dto.DailyGoalDTO;
import itstime.reflog.goal.service.DailyGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Tag(name = "DAILY GOAL API", description = "오늘의 학습 목표에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/learn")
public class DailyGoalController {
    private final DailyGoalService dailyGoalService;

    @Operation(
            summary = "오늘의 학습 목표 생성 API",
            description = "오늘의 학습 목표를 생성합니다. AccessToken 필요",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "오늘의 학습 목표 생성 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))

                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "잘못된 요청 데이터",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @PostMapping("/dailyGoal")
    public ResponseEntity<CommonApiResponse<Void>> createDailyGoal(
            @RequestParam Long memberId,
            @RequestBody DailyGoalDTO.DailyGoalSaveOrUpdateRequest dto){
            dailyGoalService.createDailyGoal(memberId, dto);
            return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "오늘의 학습 목표 조회 API",
            description = "특정 날짜에 대한 학습 목표를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "학습목표 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 학습목표를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @GetMapping("/dailyGoal")
    public ResponseEntity<CommonApiResponse<DailyGoalDTO.DailyGoalResponse>> getDailyGoalByMemberAndDate(
            @RequestParam Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate date
            ){
        DailyGoalDTO.DailyGoalResponse dailyGoal = dailyGoalService.getDailyGoalByMemberIdAndDate(memberId, date);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(dailyGoal));
    }

    @Operation(
            summary = "오늘의 학습 목표 수정 API",
            description = "오늘의 학습 목표를 수정합니다.",
            parameters = {
                    @Parameter(
                            name = "createdDate",
                            description = "수정하려는 학습 목표 날짜",
                            required = true
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "학습목표 수정 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 날짜에 생성된 학습목표를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @PatchMapping("/dailyGoal/{createdDate}")
    public ResponseEntity<CommonApiResponse<DailyGoalDTO.DailyGoalResponse>> getDailyGoalByMemberIdAndDate(
            @RequestParam Long memberId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)LocalDate createdDate,
            @RequestBody @Valid DailyGoalDTO.DailyGoalSaveOrUpdateRequest request
            ){
        dailyGoalService.updateDailyGoal(createdDate, memberId, request);
        return ResponseEntity.ok(CommonApiResponse.onSuccess((null))
        );
    }
}
