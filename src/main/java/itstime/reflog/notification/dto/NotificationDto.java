package itstime.reflog.notification.dto;

import itstime.reflog.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class NotificationDto {

    @Getter
    @AllArgsConstructor
    public static class NotificationResponse {
        private String message;
        private NotificationType type;
        private String url;
    }
}
