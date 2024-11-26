package itstime.reflog.goal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GoalDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    private static class GoalSaveOrUpdateRequest{
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
