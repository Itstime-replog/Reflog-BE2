package itstime.reflog.community.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
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


}
