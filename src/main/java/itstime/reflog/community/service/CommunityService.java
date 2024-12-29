package itstime.reflog.community.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.domain.UploadedFile;
import itstime.reflog.community.dto.CommunityDto;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.community.repository.UploadedFileRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.s3.AmazonS3Manager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityService {
	private final CommunityRepository communityRepository;
	private final UploadedFileRepository uploadedFileRepository;
	private final AmazonS3Manager amazonS3Manager;
	private final MemberRepository memberRepository;

	@Transactional
	public void createCommunity(Long memberId, CommunityDto.CommunitySaveOrUpdateRequest dto) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

		Community community = Community.builder()
			.title(dto.getTitle())
			.content(dto.getContent())
			.postTypes(dto.getPostTypes())
			.learningTypes(dto.getLearningTypes())
			.member(member)
			.createdAt(LocalDateTime.now())
			.build();

		Community savedCommunity = communityRepository.save(community);

		dto.getFileUrls().forEach(tempUrl -> {
			String fileKey = amazonS3Manager.extractFileKeyFromUrl(tempUrl);

			String newKey = fileKey.replace("temporary/", "community/");

			amazonS3Manager.moveFile(fileKey, newKey);

			UploadedFile uploadedFile = UploadedFile.builder()
				.fileName(newKey.substring(newKey.lastIndexOf("/") + 1))
				.fileUrl(amazonS3Manager.getFileUrl(newKey))
				.community(savedCommunity)
				.build();
			uploadedFileRepository.save(uploadedFile);
		});
	}
}
