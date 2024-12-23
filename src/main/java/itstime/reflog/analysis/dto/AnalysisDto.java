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

    @Data
    public static class Good {
        private String content;
        private int percentage;

        public Good(String content, int percentage) {
            this.content = content;
            this.percentage = percentage;
        }

    }
    @Data
    public static class Bad {
        private String content;
        private int percentage;

        public Bad(String content, int percentage) {
            this.content = content;
            this.percentage = percentage;
        }
    }
    @Data
    public static class Achievement {
        private String day;
        private int percentage;

        public Achievement(String day, int percentage) {
            this.day = day;
            this.percentage = percentage;
        }
    }
    @Data
    public static class StudyTypes {
        private int totalType;
        private List<StudyType> type;

        public StudyTypes(int totalType, List<StudyType> type) {
            this.totalType = totalType;
            this.type = type;
        }
    }
    @Data
    public static class StudyType {
        private String type;
        private int percentage;

        public StudyType(String type, int percentage) {
            this.type = type;
            this.percentage = percentage;
        }
    }
    /*public static class Improvement {
        private String content;
        private int rank;

        public Improvement(String content, int rank) {
            this.content = content;
            this.rank = rank;
        }
    }*/

    @Data
    public static class UnderstandingLevel {
        private String day;
        private int percentage;

        public UnderstandingLevel(String day, int percentage) {
            this.day = day;
            this.percentage = percentage;
        }
    }
}
