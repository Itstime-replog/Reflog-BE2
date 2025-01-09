package itstime.reflog.mypage.repository;

import itstime.reflog.mypage.domain.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    Optional<NotificationSettings> findByMemberId(Long memberId);
}
