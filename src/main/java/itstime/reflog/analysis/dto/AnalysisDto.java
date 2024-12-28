package itstime.reflog.analysis.dto;

import lombok.*;

import java.util.List;


public class AnalysisDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class AnalysisDtoResponse {
        private int todos;
        private int completedTodos;
        private int retrospects;
        private List<Good> goods;
        private List<Bad> bads;
        private List<Achievement> achievements;
        private List<UnderstandingLevel> understandingLevels;
        private StudyTypes types;
        //private List<Improvement>Improvements;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Good {
        private String content;
        private int percentage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bad {
        private String content;
        private int percentage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Achievement {
        private String day;
        private int percentage;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyTypes {
        private int totalType;
        private List<StudyType> type;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudyType {
        private String type;
        private int percentage;

    }
    /*public static class Improvement {
        private String content;
        private int rank;

        public Improvement(String content, int rank) {
            this.content = content;
            this.rank = rank;
        }
    }*/

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnderstandingLevel {
        private String day;
        private int percentage;
    }
}
