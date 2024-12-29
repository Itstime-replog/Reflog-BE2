package itstime.reflog.mission.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserMission;
import itstime.reflog.mission.repository.UserMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final UserMissionRepository userMissionRepository;
    private final BadgeService badgeService;

    @Transactional
    public void incrementMissionProgress(Long memberId, Badge badge) {
        UserMission userMission = userMissionRepository.findByMyPage_MemberIdAndBadge(memberId, badge)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MISSION_NOT_FOUND));

        // 미션 개수 증가
        userMission.incrementProgress();
        userMissionRepository.save(userMission);

        // 미션 완료 여부 확인 및 배지 지급
        if (userMission.isCompleted()) {
            badgeService.awardBadge(memberId, badge);
        }
    }
}
