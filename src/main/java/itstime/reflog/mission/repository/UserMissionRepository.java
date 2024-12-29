package itstime.reflog.mission.repository;

import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserMission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    Optional<UserMission> findByMyPage_MemberIdAndBadge(Long memberId, Badge badge);
}
