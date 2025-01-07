package itstime.reflog.analysis.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.analysis.service.MonthlyAnalysisService;
import itstime.reflog.analysis.service.WeeklyAnalysisService;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.common.annotation.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Tag(name = "ANALYSIS API", description = "분석보고서에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AnalysisController {
    private final WeeklyAnalysisService weeklyAnalysisService;
    private final MonthlyAnalysisService monthlyAnalysisService;

    @Operation(
            summary = "주간 분석 보고서 조회 API",
            description = "매주 월요일 날짜에 해당하는 주간 분석 보고서를 조회합니다. 개선점 키워드는 비율 높은순으로 정렬되어있습니다. 12/16 요청시 16~22에 해당하는 데이터 반환",
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
    @GetMapping("/weekly-analysis")
    public ResponseEntity<CommonApiResponse<AnalysisDto.AnalysisDtoResponse>> getWeeklyAnalysis(
            @UserId String memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        AnalysisDto.AnalysisDtoResponse analysis = weeklyAnalysisService.getWeeklyAnalysisReport(memberId, date);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(analysis));
    }
    @Operation(
            summary = "월간 분석 보고서 조회 API",
            description = "매달 1일 날짜에 해당하는 월간 분석 보고서를 조회합니다. 개선점 키워드는 비율 높은순으로 정렬되어있습니다. 12/1 요청시 1~31에 해당하는 데이터 반환",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "월간 보고서 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "해당 회원 또는 해당 월의 월간 분석보고서를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }

    )
    @GetMapping("/monthly-analysis")
    public ResponseEntity<CommonApiResponse<AnalysisDto.AnalysisDtoResponse>> getMonthlyAnalysis(
            @UserId String memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ){
        AnalysisDto.AnalysisDtoResponse analysis = monthlyAnalysisService.getMonthlyAnalysisReport(memberId, date);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(analysis));
    }
}
