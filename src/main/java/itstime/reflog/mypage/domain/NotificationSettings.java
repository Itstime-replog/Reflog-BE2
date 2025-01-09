package itstime.reflog.mypage.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import itstime.reflog.member.domain.Member;
import itstime.reflog.notification.domain.NotificationType;
import jakarta.persistence.*;
import lombok.*;

import java.util.EnumMap;
import java.util.Map;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
public class NotificationSettings {

    @Id
    @Column(name = "notificationSettings_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    @JsonIgnore
    private Member member;

    @ElementCollection
    @CollectionTable(name = "notification_settings_map", joinColumns = @JoinColumn(name = "settings_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "is_enabled")
    private Map<NotificationType, Boolean> notificationPreferences = new EnumMap<>(NotificationType.class);

    private boolean allNotificationsEnabled = true;
}
