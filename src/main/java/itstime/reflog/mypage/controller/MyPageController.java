package itstime.reflog.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.mypage.dto.MyPageDto;
import itstime.reflog.mypage.service.MyPageService;
import itstime.reflog.schedule.dto.ScheduleDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "MYPAGE API", description = "마이페이지에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/plan/mypage")
public class MyPageController {

    private final MyPageService myPageService;

    @Operation(
            summary = "마이페이지 내 정보 조회 API",
            description = "특정 회원에 해당하는 마이페이지 내 정보를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 내 정보 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 마이페이지 내 정보를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @GetMapping("/myinfo")
    public ResponseEntity<CommonApiResponse<MyPageDto.MyPageInfoResponse>> getMyInformation(
            @RequestParam Long memberId
    ) {
        MyPageDto.MyPageInfoResponse myInfo = myPageService.getMyInformation(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(myInfo));
    }

    @Operation(
            summary = "마이페이지 프로필 생성 API",
            description = "특정 회원에 해당하는 마이페이지 프로필을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 프로필 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 마이페이지 프로필을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @PostMapping("/myinfo/profile")
    public ResponseEntity<CommonApiResponse<Void>> createProfile(
            @RequestParam Long memberId,
            @Valid @RequestBody MyPageDto.MyPageProfileRequest dto
    ) {
        myPageService.createProfile(memberId, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "마이페이지 프로필 조회 API",
            description = "특정 회원에 해당하는 마이페이지 프로필을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 프로필 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 마이페이지 프로필을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @GetMapping("/myinfo/profile")
    public ResponseEntity<CommonApiResponse<MyPageDto.MyPageProfileResponse>> getProfile(
            @RequestParam Long memberId
    ) {
        MyPageDto.MyPageProfileResponse profile = myPageService.getProfile(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(profile));
    }

//    @Operation(
//            summary = "마이페이지 미션/배지 조회 API",
//            description = "특정 회원에 해당하는 마이페이지 미션/배지를 조회합니다.",
//            responses = {
//                    @ApiResponse(
//                            responseCode = "200",
//                            description = "마이페이지 미션/배지 조회 성공",
//                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
//                    ),
//                    @ApiResponse(
//                            responseCode = "404",
//                            description = "해당 회원 또는 마이페이지 미션/배지를 찾을 수 없음",
//                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
//                    ),
//                    @ApiResponse(
//                            responseCode = "500",
//                            description = "서버 에러",
//                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
//                    )
//            }
//    )
//    @GetMapping("/")
//    public ResponseEntity<CommonApiResponse<MyPageDto.MyPageMissionResponse>> getMission(
//            @RequestParam Long memberId
//    ) {
//        MyPageDto.MyPageMissionResponse mission = myPageService.getMission(memberId);
//        return ResponseEntity.ok(CommonApiResponse.onSuccess(mission));
//    }
}
