package itstime.reflog.mypage.dto;

import itstime.reflog.notification.domain.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class NotificationSettingsDto {

    @Getter
    @Setter
    public static class NotificationSettingsRequest {
        private boolean allNotificationsEnabled;
        private Map<NotificationType, Boolean> preferences;
    }


    @Getter
    @AllArgsConstructor
    public static class NotificationSettingsResponse {
        private boolean allNotificationsEnabled;
        private Map<NotificationType, Boolean> preferences;
    }
}
