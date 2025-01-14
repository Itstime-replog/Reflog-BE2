package itstime.reflog.mypage.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mypage.domain.NotificationSettings;
import itstime.reflog.mypage.dto.NotificationSettingsDto;
import itstime.reflog.mypage.repository.NotificationSettingsRepository;
import itstime.reflog.notification.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationSettingsService {
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final MemberServiceHelper memberServiceHelper;

    @Transactional
    public void updateNotificationSettings(String memberId, Map<NotificationType, Boolean> preferences, boolean allNotificationsEnabled) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 알림 설정 조회
        NotificationSettings settings =  notificationSettingsRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOTIFICATIONSETTINGS_NOT_FOUND));

        // 3. 전체 알림 설정 업데이트
        settings.setAllNotificationsEnabled(allNotificationsEnabled);

        // 4. 각 알림 설정 업데이트
        if (preferences != null) {
            settings.getNotificationPreferences().putAll(preferences);
        }

        notificationSettingsRepository.save(settings);
    }

    @Transactional
    public NotificationSettingsDto.NotificationSettingsResponse getSettings(String memberId) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 알림 설정 조회
        NotificationSettings settings =  notificationSettingsRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOTIFICATIONSETTINGS_NOT_FOUND));

        System.out.println("Settings Found: " + settings);
        System.out.println("All Notifications Enabled: " + settings.isAllNotificationsEnabled());
        System.out.println("Preferences: " + settings.getNotificationPreferences());


        return new NotificationSettingsDto.NotificationSettingsResponse(
                settings.isAllNotificationsEnabled(),
                settings.getNotificationPreferences()
        );
    }


        void createDefaultSettings(Member member) {
        // 1. 알림 설정 생성
        NotificationSettings settings = new NotificationSettings();
        settings.setMember(member);
        settings.setAllNotificationsEnabled(true);

        // 2. 알림 설정 기본값 설정
        Map<NotificationType, Boolean> defaultPreferences = new EnumMap<>(NotificationType.class);
        for (NotificationType type : NotificationType.values()) {
            defaultPreferences.put(type, true);
        }
        settings.setNotificationPreferences(defaultPreferences);

        notificationSettingsRepository.save(settings);
    }
}
