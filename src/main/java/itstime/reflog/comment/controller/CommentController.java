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
    @PostMapping("/{postId}")
    public ResponseEntity<CommonApiResponse<Void>> createComment(
            @PathVariable Long postId,
            @UserId String memberId,
            @RequestBody CommentDto.CommentSaveOrUpdateRequest dto
    ) {
        commentService.createComment(postId, memberId, dto);
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
    @PatchMapping("/{commentId}")
    public ResponseEntity<CommonApiResponse<Void>> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto.CommentSaveOrUpdateRequest dto
    ) {
        commentService.updateComment(commentId, dto);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "댓글 삭제 API",
            description = "댓글을 삭제합니다. AccessToken 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "댓글 삭제 성공"
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
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonApiResponse<Void>> deleteComment(
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }

    @Operation(
            summary = "댓글 좋아요 API",
            description = "댓글에 좋아요를 합니다. AccessToken 필요.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "댓글 좋아요 성공"
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
    @PostMapping("/like/{commentId}")
    public ResponseEntity<CommonApiResponse<Void>> toggleCommentLike(
            @PathVariable Long commentId,
            @UserId String memberId
    ) {
        commentService.toggleCommentLike(commentId, memberId);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }
}
