package itstime.reflog.community.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
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
	public static class CommunityCategoryResponse {

		private String title;
		private String content;
		private LocalDateTime createdDate;
		private List<String> postTypes;
		private List<String> learningTypes;
		private String writer;

		public static CommunityCategoryResponse fromEntity(Community community, String writer) {
			return CommunityCategoryResponse.builder()
					.title(community.getTitle())
					.createdDate(community.getCreatedAt())
					.postTypes(community.getPostTypes())
					.learningTypes(community.getLearningTypes())
					.writer(writer)
					.build();
		}
	}
}
