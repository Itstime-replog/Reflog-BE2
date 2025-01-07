package itstime.reflog.mission.repository;

import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserMission;
import itstime.reflog.mypage.domain.MyPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserMissionRepository extends JpaRepository<UserMission, Long> {

    Optional<UserMission> findByMyPageAndBadge(MyPage myPage, Badge badge);
}
