package itstime.reflog.analysis.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        //private List<StudyType> types;
        private List<Improvement>Improvements;
    }

    public static class Good {
        private String content;
        private int percentage;

        public Good(String content, int percentage) {
            this.content = content;
            this.percentage = percentage;
        }

    }
    public static class Bad {
        private String content;
        private int percentage;

        public Bad(String content, int percentage) {
            this.content = content;
            this.percentage = percentage;
        }
    }
    public static class Achievement {
        private String day;
        private int percentage;

        public Achievement(String day, int percentage) {
            this.day = day;
            this.percentage = percentage;
        }
    }
    public static class StudyType {
        private String type;
        private int percentage;

        public StudyType(String type, int percentage) {
            this.type = type;
            this.percentage = percentage;
        }
    }
    public static class Improvement {
        private String content;
        private int rank;

        public Improvement(String content, int rank) {
            this.content = content;
            this.rank = rank;
        }
    }
}
