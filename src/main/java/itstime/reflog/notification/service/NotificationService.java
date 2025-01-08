package itstime.reflog.notification.service;

import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
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
        // Save the notification to the database
        Notification notification = Notification.builder()
                .memberId(memberId)
                .message(message)
                .url(url)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // Send the notification via SSE if the user is connected
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
