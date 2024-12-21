package itstime.reflog.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.analysis.service.AnalysisService;
import itstime.reflog.common.CommonApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "ANALYSIS APLI", description = "분석보고서에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AnalysisController {
    private final AnalysisService analysisService;

    @Operation(
            summary = "주간 분석 보고서 조회 API",
            description = "특정 날짜에 해당하는 주간 분석 보고서를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "주간 보고서 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "해당 회원 또는 해당 주의 주간 분석보고서를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }

    )
    @GetMapping("/analysis")
    public ResponseEntity<CommonApiResponse<AnalysisDto.AnalysisDtoResponse>> getWeeklyAnalysis(
            @RequestParam Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        AnalysisDto.AnalysisDtoResponse analysis = analysisService.getWeeklyTodoList(memberId, date);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(analysis));
    }
}
