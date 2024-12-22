package itstime.reflog.retrospect.dto;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RetrospectDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RetrospectSaveRequest {

		@NotBlank(message = "제목은 비어 있을 수 없습니다.")
		private String title; // 제목

		@NotNull(message = "작성일은 필수입니다.")
		private LocalDate createdDate; // 작성일

		@NotBlank(message = "학습 유형은 비어 있을 수 없습니다.")
		private String studyType; // 학습 유형

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
}
