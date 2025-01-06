package itstime.reflog.community.controller;

import java.util.List;
import java.util.UUID;

import itstime.reflog.postlike.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.community.service.CommunityService;
import itstime.reflog.s3.AmazonS3Manager;
import itstime.reflog.community.dto.CommunityDto;
import lombok.RequiredArgsConstructor;

@Tag(name = "COMMUNITY API", description = "커뮤니티에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities")
public class CommunityController {
	private final CommunityService communityService;
	private final AmazonS3Manager amazonS3Manager;
	private final PostLikeService postLikeService;

	@Operation(
		summary = "커뮤니티 파일 임시 생성 API",
		description = "커뮤니티 게시글 작성 시에 등록 전에도 사진이 보여져야 하기 때문에 파일 임시 생성 API를 만들었습니다. 만약 게시글 작성할 때 파일을 업로드하게 된다면 이 API를 사용해주세요. AccessToken 필요.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "커뮤니티 파일 임시 생성 성공"
			),
			@ApiResponse(
				responseCode = "500",
				description = "서버 에러"
			)
		}
	)
	@PostMapping(value = "/temp-files", consumes = "multipart/form-data")
	public ResponseEntity<CommonApiResponse<String>> uploadTempFile(
		@RequestPart MultipartFile file) {
		// S3에 임시 저장
		String fileUrl = amazonS3Manager.uploadFile("temporary/" + UUID.randomUUID(), file);

		// URL 반환
		return ResponseEntity.ok(CommonApiResponse.onSuccess(fileUrl));
	}

	@Operation(
		summary = "커뮤니티 생성 API",
		description = "커뮤니티 게시글 생성 API입니다. 게시글 등록할 때 파일을 업로드하게 되면 커뮤니티 파일 임시 생성 API에서 받은 url값을 fileUrls에 전달해주시면 돼요. AccessToken 필요.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "커뮤니티 게시글 생성 성공"
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
	public ResponseEntity<CommonApiResponse<Void>> createCommunity(
		@RequestParam Long memberId,
		@RequestBody CommunityDto.CommunitySaveOrUpdateRequest dto) {
		communityService.createCommunity(memberId, dto);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

	@Operation(
		summary = "커뮤니티 수정 API",
		description = "커뮤니티 게시글 수정 API입니다. 마찬가지로 게시글 수정할 때 파일을 업로드하게 되면 커뮤니티 파일 임시 생성 API에서 받은 url값을 fileUrls에 전달해주시면 돼요. AccessToken 필요.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "커뮤니티 게시글 수정 성공"
			),
			@ApiResponse(
				responseCode = "400",
				description = "S3 파일 URL이 잘못되었거나 존재X"
			),
			@ApiResponse(
				responseCode = "500",
				description = "서버 에러"
			)
		}
	)
	@PatchMapping
	public ResponseEntity<CommonApiResponse<Void>> updateCommunity(
		@RequestParam Long communityId,
		@RequestBody CommunityDto.CommunitySaveOrUpdateRequest dto) {
		communityService.updateCommunity(communityId, dto);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

	@Operation(
		summary = "커뮤니티 삭제 API",
		description = "커뮤니티 게시글 삭제 API입니다. AccessToken 필요.",
		responses = {
			@ApiResponse(
				responseCode = "200",
				description = "커뮤니티 게시글 삭제 성공"
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
	@DeleteMapping
	public ResponseEntity<CommonApiResponse<Void>> deleteCommunity(
		@RequestParam Long communityId) {
		communityService.deleteCommunity(communityId);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}

	//학습 유형 별 필터링 api
	@Operation(
			summary = "커뮤니티 게시글 카테고리별 조회 필터링 API",
			description = "카테고리별 커뮤니티 게시글을 조회합니다. 카테고리를 파라미터에 입력하면 해당하는 게시글들 반환. 기타는 파라미터에 기타를 입력하면 됩니다. postTypes에는 글 유형, learningTypes에는 학습 유형을 각각 최대 두개까지 입력할 수 있습니다." +
					"postTypes이 회고일지일 경우 회고일지를 반환합니다 understandinglevel,progresslevel도 반환 회고일지 아닌 경우 null",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "카테고리별 커뮤니티 게시글 조회 성공"
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
	public ResponseEntity<CommonApiResponse<List<CommunityDto.CombinedCategoryResponse>>> getFilteredCommunity(
			@RequestParam Long memberId,
			@RequestParam(required = false) List<String> postTypes,
			@RequestParam(required = false) List<String> learningTypes
			){
		List<CommunityDto.CombinedCategoryResponse> responses = communityService.getFilteredCommunity(memberId,postTypes, learningTypes);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(responses));
	}

	@Operation(
			summary = "커뮤니티 게시글 검색 API",
			description = "카테고리별 커뮤니티 게시글을 검색합니다. 파라미터에 검색하고자 하는 string을 입력하면 일치하는 게시물을 반환합니다",
			responses = {
					@ApiResponse(
							responseCode = "200",
							description = "커뮤니티 게시글 검색 성공"
					),
					@ApiResponse(
							responseCode = "404",
							description = "입력하신 제목과 일치하는 게시물을 찾을 수 없음"
					),
					@ApiResponse(
							responseCode = "500",
							description = "서버 에러"
					)
			}
	)
	@GetMapping("/search")
	public ResponseEntity<CommonApiResponse<List<CommunityDto.CombinedCategoryResponse>>> getSearchedCommunity(
			@RequestParam Long memberId,
			@RequestParam String title
	){
		List<CommunityDto.CombinedCategoryResponse> responses = communityService.getSearchedCommunity(memberId,title);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(responses));
	}

	@Operation(
			summary = "커뮤니티 게시물 좋아요 API",
			description = "커뮤니티 게시글 좋아요 버튼 누를때 사용하는 API입니다. 현재 버튼이 좋아요가 아닌 상태=false, 좋아요 버튼을 누르면 true를 보내주면 됩니다.",
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
	@PostMapping("/like/{postId}")
	public ResponseEntity<CommonApiResponse<Void>> togglePostLike(
			@RequestParam Long memberId,
			@PathVariable Long postId,
			@RequestParam String postType
	){
		postLikeService.togglePostLike(memberId, postId, postType);
		return ResponseEntity.ok(CommonApiResponse.onSuccess(null));
	}


}
