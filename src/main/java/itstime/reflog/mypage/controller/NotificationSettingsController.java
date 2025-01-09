package itstime.reflog.mypage.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.common.annotation.UserId;
import itstime.reflog.mypage.dto.NotificationSettingsDto;
import itstime.reflog.mypage.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "NotificationSettings API", description = "알림설정에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications/settings")
public class NotificationSettingsController {

    private final NotificationSettingsService notificationSettingsService;

    @Operation(
            summary = "마이페이지 내 알림 설정 수정 API",
            description = "특정 회원에 해당하는 마이페이지 내 알림 설정을 수정합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 내  알림 설정 수정 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 마이페이지 내  알림 설정을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @PutMapping("")
    public ResponseEntity<CommonApiResponse<Void>> updateSettings(
            @UserId String memberId,
            @RequestBody NotificationSettingsDto.NotificationSettingsRequest request
    ) {
        notificationSettingsService.updateNotificationSettings(memberId, request.getPreferences(), request.isAllNotificationsEnabled());
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "마이페이지 내 알림 설정 조회 API",
            description = "특정 회원에 해당하는 마이페이지 내 알림 설정을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "마이페이지 내  알림 설정 조회 성공",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 회원 또는 마이페이지 내  알림 설정을 찾을 수 없음",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "서버 에러",
                            content = @Content(schema = @Schema(implementation = CommonApiResponse.class))
                    )
            }
    )
    @GetMapping("")
    public ResponseEntity<CommonApiResponse<NotificationSettingsDto.NotificationSettingsResponse>> getSettings(
            @UserId String memberId
    ) {
        NotificationSettingsDto.NotificationSettingsResponse response = notificationSettingsService.getSettings(memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(response));
    }
}
