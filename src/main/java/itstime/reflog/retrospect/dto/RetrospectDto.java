package itstime.reflog.retrospect.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class RetrospectDto {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class RetrospectSaveRequest {
		private String title; // 제목

		private LocalDate createdDate; // 작성일

		private int progressLevel; // 수행 정도

		private int understandingLevel; // 이해도

		private String actionPlan; // 부족한 점 개선을 위한 계획

		private boolean visibility; // 공개 여부

		private String studyType; // 학습 유형

		private List<String> goodContents; // 잘한 점 리스트

		private List<String> badContents; // 잘한 점 리스트 (Good의 content를 저장)
	}
}
