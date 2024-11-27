package itstime.reflog.goal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class DailyGoalDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DailyGoalSaveOrUpdateRequest{
        @NotBlank(message = "content는 비어 있을 수 없습니다.")
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class GoalResponse{
        private final Long goalId;
        private final String content;
    }
}
