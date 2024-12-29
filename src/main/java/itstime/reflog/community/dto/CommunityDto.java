package itstime.reflog.community.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
