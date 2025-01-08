package itstime.reflog.notification.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mypage.domain.NotificationSettings;
import itstime.reflog.mypage.repository.NotificationSettingsRepository;
import itstime.reflog.notification.domain.Notification;
import itstime.reflog.notification.domain.NotificationType;
import itstime.reflog.notification.dto.NotificationDto;
import itstime.reflog.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>(); // 사용자별 연결 관리
    private final MemberServiceHelper memberServiceHelper;


    public SseEmitter subscribe(String memberId) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L); // 3600초 = 1시간 동안 연결 유지
        emitters.put(member.getId(), emitter);

        // 연결 종료 시 제거
        emitter.onCompletion(() -> emitters.remove(member.getId()));
        emitter.onTimeout(() -> emitters.remove(member.getId()));
        emitter.onError(e -> emitters.remove(member.getId()));

        return emitter;
    }

    public void sendNotification(Long memberId, String message, NotificationType type, String url) {
        // 1. 알림 설정 확인
        NotificationSettings settings = notificationSettingsRepository.findByMemberId(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._NOTIFICATIONSETTINGS_NOT_FOUND));

        if (!settings.isAllNotificationsEnabled()) { // 전체 알림
            return;
        }

        Boolean isEnabled = settings.getNotificationPreferences().get(type); // 각 알림
        if (isEnabled != null && !isEnabled) {
            return;
        }

        // 2. 알림 저장
        Notification notification = Notification.builder()
                .memberId(memberId)
                .message(message)
                .url(url)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // 3. 유저가 연결 시 알림 보내기
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                NotificationDto.NotificationResponse response = new NotificationDto.NotificationResponse(message, type, url);
                emitter.send(SseEmitter.event().name("notification").data(response));
            } catch (Exception e) {
                emitters.remove(memberId); // 실패 시 연결 제거
            }
        }
    }
}
