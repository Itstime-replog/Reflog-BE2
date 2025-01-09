package itstime.reflog.schedule.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class ScheduleDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ScheduleSaveOrUpdateRequest {

        @NotBlank(message = "title은 비어 있을 수 없습니다.")
        private String title;

        private String content;

        private boolean allday;

        private Boolean isOn;

        @NotNull(message = "startDateTime은 비어 있을 수 없습니다.")
        private LocalDateTime startDateTime;

        @NotNull(message = "endDateTime은 비어 있을 수 없습니다.")
        private LocalDateTime endDateTime;
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleResponse {
        private final Long scheduleId;
        private final String title;
        private final String content;
        private final boolean allday;
        private Boolean isOn;
        private final LocalDateTime startDateTime;
        private final LocalDateTime endDateTime;
    }

    @Getter
    @AllArgsConstructor
    public static class ScheduleAllResponse {
        private final Long scheduleId;
        private final String title;
        private final LocalDateTime startDateTime;

    }
}
