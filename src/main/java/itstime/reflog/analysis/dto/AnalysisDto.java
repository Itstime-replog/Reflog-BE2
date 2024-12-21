package itstime.reflog.analysis.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


public class AnalysisDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class AnalysisDtoResponse {
        private int todos;
        private int completedTodos;
    }
}
