package itstime.reflog.goal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class DailyGoalDTO {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class DailyGoalSaveOrUpdateRequest{
        @NotBlank(message = "content는 비어 있을 수 없습니다.")
        private String content;
    }

    @Getter
    @AllArgsConstructor
    public static class DailyGoalResponse{
        private final LocalDate dailyGoalId;
        private final String content;
    }
}
