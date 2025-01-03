package itstime.reflog.community.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.domain.StudyType;
import lombok.*;

public class CommunityDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CommunitySaveOrUpdateRequest {
		private String title;
		private String content;
		private List<String> postTypes;
		private List<String> learningTypes;
		private List<String> fileUrls;
	}

	//카테고리 별 필터링 api dto
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CombinedCategoryResponse {

		private String title;
		private LocalDateTime createdDate; // Retrospect는 LocalDate로 변경 가능
		private List<String> postTypes;
		private List<String> learningTypes;
		private String writer;
		private Integer progressLevel;    // Retrospect 전용
		private Integer understandingLevel; // Retrospect 전용

		public static CombinedCategoryResponse fromCommunity(Community community, String writer) {
			return CombinedCategoryResponse.builder()
					.title(community.getTitle())
					.createdDate(community.getCreatedAt())
					.postTypes(community.getPostTypes())
					.learningTypes(community.getLearningTypes())
					.writer(writer)
					.build();
		}

		public static CombinedCategoryResponse fromRetrospect(Retrospect retrospect, String writer) {
			return CombinedCategoryResponse.builder()
					.title(retrospect.getTitle())
					.createdDate(retrospect.getCreatedDate().atStartOfDay())
					.postTypes(List.of("회고일지")) // 단일 값을 리스트로 변환
					.learningTypes(retrospect.getStudyTypes().stream()
							.map(studyType -> studyType.getType()) // StudyType을 String으로 변환
							.collect(Collectors.toList())) //String 리스트로 변환
					.progressLevel(retrospect.getProgressLevel())
					.understandingLevel(retrospect.getUnderstandingLevel())
					.writer(writer)
					.build();
		}
	}
}
