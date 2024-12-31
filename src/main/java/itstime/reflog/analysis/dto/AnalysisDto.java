package itstime.reflog.analysis.dto;

import itstime.reflog.analysis.domain.WeeklyAnalysis;
import itstime.reflog.analysis.domain.enums.GoodBad;
import itstime.reflog.analysis.domain.enums.Period;
import itstime.reflog.analysis.domain.enums.UnderstandingAchievement;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.domain.StudyType;
import itstime.reflog.retrospect.dto.RetrospectDto;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


public class AnalysisDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class AnalysisDtoResponse {
        private int todos;
        private int completedTodos;
        private int retrospects;
        private long totalStudyTypes;
        private List<Achievement> achievements;
        private List<UnderstandingLevel> understandingLevels;
        private List<GoodResponse> goods;
        private List<BadResponse> bads;
        private List<StudyTypeResponse> studyTypes;
        private LocalDate startDate;
        private List<String>improvements;
        private Period period;

        public static AnalysisDto.AnalysisDtoResponse fromEntity(WeeklyAnalysis analysis) {
            return AnalysisDtoResponse.builder()
                    .todos(analysis.getTodos())
                    .completedTodos(analysis.getCompletedTodos())
                    .retrospects(analysis.getRetrospects())
                    .totalStudyTypes(analysis.getTotalStudyType())
                    .startDate(analysis.getStartDate())
                    .improvements(analysis.getImprovements().stream().map(content ->
                            content.getContent()).toList())
                    .goods(
                            analysis.getAnalysisGoodsBads().stream()
                                    .filter(goodBad -> goodBad.getGoodBad() == GoodBad.GOOD)
                                    .map(goodBad -> new GoodResponse(goodBad.getContent(), goodBad.getPercentage())) // 매핑 로직 추가
                                    .collect(Collectors.toList())
                    )
                    .bads(
                            analysis.getAnalysisGoodsBads().stream()
                                    .filter(goodBad -> goodBad.getGoodBad() == GoodBad.BAD)
                                    .map(goodBad -> new BadResponse(goodBad.getContent(), goodBad.getPercentage())) // 매핑 로직 추가
                                    .collect(Collectors.toList())
                    )
                    .achievements(
                            analysis.getAnalysisUnderstandingAchievements().stream()
                                    .filter(understandingAchievement -> understandingAchievement.getUnderstandingAchievement() == UnderstandingAchievement.ACHIEVEMENT)
                                    .map(understandingAchievement -> new Achievement(understandingAchievement.getDay(), understandingAchievement.getPercentage())) // 매핑 로직 추가
                                    .collect(Collectors.toList())
                    )
                    .understandingLevels(
                            analysis.getAnalysisUnderstandingAchievements().stream()
                                    .filter(understandingAchievement -> understandingAchievement.getUnderstandingAchievement() == UnderstandingAchievement.UNDERSTANDING)
                                    .map(understandingAchievement -> new UnderstandingLevel(understandingAchievement.getDay(), understandingAchievement.getPercentage())) // 매핑 로직 추가
                                    .collect(Collectors.toList())
                    )
                    .studyTypes(analysis.getStudyTypes().stream()
                            .map(study -> new StudyTypeResponse(study.getType(), study.getPercentage()))
                            .collect(Collectors.toList()))
                    .period(analysis.getPeriod()
                    )
                    .build();
        }

        }

    // 요일별 수행도 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Achievement {
        private String day;  // 요일
        private int percentage;  // 수행도 퍼센트
    }

    // 요일별 이해도 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UnderstandingLevel {
        private String day;  // 요일
        private int percentage;  // 이해도 퍼센트
    }

    // 잘한 점 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GoodResponse {
        private String content;  // 잘한 점 내용
        private int percentage;  // 비율
    }

    // 부족한 점 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BadResponse {
        private String content;  // 부족한 점 내용
        private int percentage;  // 비율
    }

    // 학습 유형 응답 DTO
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class StudyTypeResponse {
        private String type;  // 학습 유형
        private int percentage;  // 비율
    }

}
