package itstime.reflog.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.comment.dto.CommentDto;
import itstime.reflog.comment.service.CommentService;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.common.annotation.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "COMMENT API", description = "댓글에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "댓글 생성 API",
            description = "새로운 댓글을 생성합니다. AccessToken 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "댓글 생성 성공"
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
    @PostMapping("/{communityId}")
    public ResponseEntity<CommonApiResponse<Void>> createComment(
            @PathVariable Long communityId,
            @UserId String memberId,
            @RequestBody CommentDto.CommentSaveOrUpdateRequest dto
    ) {
        commentService.createComment(communityId, memberId, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "댓글 수정 API",
            description = "댓글을 수정합니다. AccessToken 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "댓글 수정 성공"
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
    @PatchMapping("/{communityId}/{commentId}")
    public ResponseEntity<CommonApiResponse<Void>> updateComment(
            @PathVariable Long communityId,
            @PathVariable Long commentId,
            @UserId String memberId,
            @RequestBody CommentDto.CommentSaveOrUpdateRequest dto
    ) {
        commentService.updateComment(communityId, commentId, memberId, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }
}
