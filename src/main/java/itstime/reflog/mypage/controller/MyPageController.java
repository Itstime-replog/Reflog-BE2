package itstime.reflog.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.common.annotation.UserId;
import itstime.reflog.mypage.dto.MyPageDto;
import itstime.reflog.mypage.service.MyPageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MYPAGE API", description = "마이페이지에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
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
            @UserId String memberId
    ) {
        MyPageDto.MyPageInfoResponse myInfo = myPageService.getMyInformation(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(myInfo));
    }

    @Operation(
            summary = "마이페이지 프로필 생성 API",
            description = "특정 회원에 해당하는 마이페이지 프로필을 생성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 프로필 생성 성공",
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
            @UserId String memberId,
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
            @UserId String memberId
    ) {
        MyPageDto.MyPageProfileResponse profile = myPageService.getProfile(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(profile));
    }

    @Operation(
            summary = "마이페이지 프로필 수정 API",
            description = "특정 회원에 해당하는 마이페이지 프로필을 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 프로필 수정 성공",
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
    @PatchMapping("/myinfo/profile")
    public ResponseEntity<CommonApiResponse<Void>> updateProfile(
            @UserId String memberId,
            @Valid @RequestBody MyPageDto.MyPageProfileRequest dto
    ) {
        myPageService.updateProfile(memberId, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "마이페이지 커뮤니티 활동 로그 - 내가 작성한 글 API",
            description = "특정 회원에 해당하는 마이페이지 커뮤니티 활동 로그에서 '내가 작성한 글'을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 커뮤니티 활동 로그 - 내가 작성한 글 조회 성공",
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
    @GetMapping("/communitylog/posts")
    public ResponseEntity<CommonApiResponse<List<MyPageDto.MyPagePostResponse>>> getMyPost(
            @UserId String memberId
    ) {
        List<MyPageDto.MyPagePostResponse> responses = myPageService.getMyPost(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(responses));
    }

    @Operation(
            summary = "마이페이지 커뮤니티 활동 로그 - 내가 좋아요 한 글 API",
            description = "특정 회원에 해당하는 마이페이지 커뮤니티 활동 로그에서 '내가 좋아요 한 글'을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 커뮤니티 활동 로그 - 내가 좋아요 한 글 조회 성공",
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
    @GetMapping("/communitylog/like/posts")
    public ResponseEntity<CommonApiResponse<List<MyPageDto.MyPagePostResponse>>> getMyLikePost(
            @UserId String memberId
    ) {
        List<MyPageDto.MyPagePostResponse> responses = myPageService.getMyLikePost(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(responses));
    }

    @Operation(
            summary = "마이페이지 미션/배지 조회 API",
            description = "특정 회원에 해당하는 마이페이지 미션/배지를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 미션/배지 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 마이페이지 미션/배지를 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @GetMapping("/badges")
    public ResponseEntity<CommonApiResponse<List<MyPageDto.MyPageBadgeResponse>>> getMyPageBadge(
            @UserId String memberId
    ) {
        List<MyPageDto.MyPageBadgeResponse> myPageBadge = myPageService.getMyPageBadge(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(myPageBadge));
    }
}
