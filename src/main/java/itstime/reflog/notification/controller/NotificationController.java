package itstime.reflog.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import itstime.reflog.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "NOTIFICATION API", description = "알림에 대한 API입니다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/subscribe/{memberId}")
    public SseEmitter subscribe(@PathVariable Long memberId) {
        return notificationService.subscribe(memberId);
    }
}
