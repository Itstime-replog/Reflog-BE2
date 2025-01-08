package itstime.reflog.retrospect.controller;

import itstime.reflog.common.annotation.UserId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.retrospect.dto.RetrospectDto;
import itstime.reflog.retrospect.service.RetrospectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Tag(name = "RETROSPECT API", description = "회고일지에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/retrospect")
public class RetrospectController {
    private final RetrospectService retrospectService;

    @Operation(
            summary = "회고일지 생성 API",
            description = "새로운 회고일지 항목을 생성합니다. AccessToken 필요. progressLevel은 수행정도, understandingLevel은 이해도, actionPlan은 7번 문항, studyType은 2번 문항",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회고일지 생성 성공"
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
    @PostMapping
    public ResponseEntity<CommonApiResponse<Void>> createRetrospect(
            @UserId String memberId,
            @Valid @RequestBody RetrospectDto.RetrospectSaveOrUpdateRequest dto
    ) {
        retrospectService.createRetrospect(memberId, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }


    @Operation(
            summary = "회고일지 조회 API",
            description = "새로운 회고일지 항목을 조회합니다. AccessToken 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회고일지 조회 성공"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회고일지를 찾을 수 없음"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<CommonApiResponse<RetrospectDto.RetrospectResponse>> getRetrospect(
            @RequestParam Long retrospectId,
            @UserId String memberId
    ) {
        RetrospectDto.RetrospectResponse response = retrospectService.getRetrospect(retrospectId, memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(response));
    }

    @Operation(
            summary = "회고일지 수정 API",
            description = "새로운 회고일지 항목을 수정합니다. AccessToken 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회고일지 수정 성공"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회고일지를 찾을 수 없음"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러"
                    )
            }
    )
    @PatchMapping
    public ResponseEntity<CommonApiResponse<Void>> updateRetrospect(
            @RequestParam Long retrospectId,
            @Valid @RequestBody RetrospectDto.RetrospectSaveOrUpdateRequest dto) {
        retrospectService.updateRetrospect(retrospectId, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    //학습 유형 별 필터링 api
    @Operation(
            summary = "회고일지 학습유형별 조회 필터링 API",
            description = "학습유형별 회고일지를 조회합니다. 학습 유형을 파라미터에 입력하면 해당하는 회고일지들 반환. 기타는 파라미터에 기타를 입력하면 됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "학습유형별 회고일지 조회 성공"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 카테고리를 찾을 수 없음"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러"
                    )
            }
    )
    @GetMapping("/filter")
    public ResponseEntity<CommonApiResponse<List<RetrospectDto.RetrospectCategoryResponse>>> getFilteredRetrospect(
            @UserId String memberId,
            @RequestParam String category
    ) {
        List<RetrospectDto.RetrospectCategoryResponse> responses = retrospectService.getFilteredRetrospect(category, memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(responses));
    }
}
