package itstime.reflog.retrospect.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import itstime.reflog.retrospect.domain.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RetrospectDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RetrospectSaveOrUpdateRequest {

		@NotBlank(message = "제목은 비어 있을 수 없습니다.")
		private String title; // 제목

		@NotNull(message = "작성일은 필수입니다.")
		private LocalDate createdDate; // 작성일

		@NotNull(message = "학습 유형 리스트는 필수입니다.")
		@Size(min = 1, message = "학습 유형은 최소 1개 이상이어야 합니다.")
		private List<String> studyTypes; // 학습 유형

		@Min(value = 0, message = "수행 정도는 0 이상이어야 합니다.")
		@Max(value = 100, message = "수행 정도는 100 이하이어야 합니다.")
		private int progressLevel; // 수행 정도

		@Min(value = 0, message = "이해도는 0 이상이어야 합니다.")
		@Max(value = 100, message = "이해도는 100 이하이어야 합니다.")
		private int understandingLevel; // 이해도

		@NotNull(message = "잘한 점 리스트는 필수입니다.")
		@Size(min = 1, message = "잘한 점 리스트는 최소 1개의 항목이 필요합니다.")
		private List<String> goodContents; // 잘한 점 리스트

		@NotNull(message = "부족한 점 리스트는 필수입니다.")
		@Size(min = 1, message = "잘못된 점 리스트는 최소 1개의 항목이 필요합니다.")
		private List<String> badContents; // 부족한 점 리스트

		@NotBlank(message = "개선 계획은 비어 있을 수 없습니다.")
		private String actionPlan; // 개선 계획(서술형)

		@NotNull(message = "공개 여부는 필수입니다.")
		private boolean visibility; // 공개 여부
	}

	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RetrospectResponse {

		private String title;
		private LocalDate createdDate;
		private List<String> studyTypes;
		private int progressLevel;
		private int understandingLevel;
		private List<String> goodContents;
		private List<String> badContents;
		private String actionPlan;
		private boolean visibility;

		public static RetrospectResponse fromEntity(Retrospect retrospect) {
			return RetrospectResponse.builder()
				.title(retrospect.getTitle())
				.createdDate(retrospect.getCreatedDate())
				.studyTypes(retrospect.getStudyTypes().stream()
					.map(StudyType::getType)
					.collect(Collectors.toList()))
				.progressLevel(retrospect.getProgressLevel())
				.understandingLevel(retrospect.getUnderstandingLevel())
				.goodContents(retrospect.getGoods().stream()
					.map(good -> good.getContent())
					.collect(Collectors.toList()))
				.badContents(retrospect.getBads().stream()
					.map(bad -> bad.getContent())
					.collect(Collectors.toList()))
				.actionPlan(retrospect.getActionPlan())
				.visibility(retrospect.isVisibility())
				.build();
		}

	}

	//학습 유형 별 필터링 api dto
	@Getter
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RetrospectCategoryResponse {

		private String title;
		private LocalDate createdDate;
		private List<String> studyTypes;
		private boolean visibility;

		public static List<RetrospectCategoryResponse> fromEntity(List<Retrospect> retrospects) {
			return retrospects.stream()
					.map(retrospect -> RetrospectCategoryResponse.builder()
							.title(retrospect.getTitle())
							.createdDate(retrospect.getCreatedDate())
							.studyTypes(retrospect.getStudyTypes().stream()
									.map(StudyType::getType)
									.collect(Collectors.toList()))
							.visibility(retrospect.isVisibility())
							.build()
					)
					.collect(Collectors.toList());
		}
	}
}
