package itstime.reflog.postlike.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.community.dto.CommunityDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "COMMUNITY LIKE API", description = "커뮤니티 게시물 좋아요 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities/like")
public class PostLikeController {
    @Operation(
            summary = "커뮤니티 게시물 좋아요 API",
            description = "커뮤니티 게시글 좋아요 누를때 사용하는 API입니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "커뮤니티 게시글 좋아요 성공"
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
    public ResponseEntity<CommonApiResponse<Void>> postLike(
            @RequestParam Long memberId,
            @RequestBody Boolean isLike
    ){
        communityService.createCommunity(memberId, isLike);
        return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
    }
}
