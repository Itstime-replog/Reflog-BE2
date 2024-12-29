package itstime.reflog.notification.service;

import itstime.reflog.notification.domain.Notification;
import itstime.reflog.notification.domain.NotificationType;
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
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(60 * 1000L); // 60 seconds timeout
        emitters.put(memberId, emitter);

        // Remove emitter on completion, timeout, or error
        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));
        emitter.onError(e -> emitters.remove(memberId));

        return emitter;
    }

    public void sendNotification(Long memberId, String message, NotificationType type) {
        // Save the notification to the database
        Notification notification = Notification.builder()
                .memberId(memberId)
                .message(message)
                .type(type)
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notification);

        // Send the notification via SSE if the user is connected
        SseEmitter emitter = emitters.get(memberId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(message));
            } catch (Exception e) {
                emitters.remove(memberId); // Remove emitter on failure
            }
        }
    }
}
