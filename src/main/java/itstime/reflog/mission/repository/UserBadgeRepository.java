package itstime.reflog.mission.repository;

import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

    Optional<UserBadge> findByMyPage_MemberIdAndBadge(Long memberId, Badge badge);
}
