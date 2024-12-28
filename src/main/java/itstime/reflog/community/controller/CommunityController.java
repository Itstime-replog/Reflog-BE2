package itstime.reflog.community.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import itstime.reflog.common.CommonApiResponse;
import itstime.reflog.community.service.CommunityService;
import itstime.reflog.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/communities")
public class CommunityController {
	private final CommunityService communityService;
	private final AmazonS3Manager amazonS3Manager;

	@PostMapping(value = "/temp-files", consumes = "multipart/form-data")
	public ResponseEntity<CommonApiResponse<String>> uploadTempFile(
		@RequestPart MultipartFile file) {
		// S3에 임시 저장
		String fileUrl = amazonS3Manager.uploadFile("temporary/" + UUID.randomUUID(), file);

		// URL 반환
		return ResponseEntity.ok(CommonApiResponse.onSuccess(fileUrl));
	}
}
