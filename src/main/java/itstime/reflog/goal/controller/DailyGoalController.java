package itstime.reflog.goal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.goal.dto.DailyGoalDTO;
import itstime.reflog.goal.service.DailyGoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
