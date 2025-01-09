package itstime.reflog.mission.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserMission;
import itstime.reflog.mission.repository.UserMissionRepository;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.notification.domain.NotificationType;
import itstime.reflog.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final UserMissionRepository userMissionRepository;
    private final BadgeService badgeService;
    private final NotificationService notificationService;


    @Transactional
    public void incrementMissionProgress(Long memberId, MyPage myPage, Badge badge) {
        // 1. 유저 미션 조회
        UserMission userMission = userMissionRepository.findByMyPageAndBadge(myPage, badge)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MISSION_NOT_FOUND));

        // 2. 유저 미션 개수 증가
        userMission.incrementProgress();
        userMissionRepository.save(userMission);

        // 3. 유저 미션 처음
        if (badge != Badge.FIRST_MEETING && badge != Badge.RETROSPECTIVE_STARTER) {
            if (badge == Badge.RETROSPECTIVE_MANIA && userMission.getProgressCount() == 10) {
                sendMissionNotification(memberId, badge, userMission);
            } else if (badge == Badge.RETROSPECTIVE_GURU && userMission.getProgressCount() == 3) {
                sendMissionNotification(memberId, badge, userMission);
            } else if (userMission.getProgressCount() == 1) {
                sendMissionNotification(memberId, badge, userMission);
            }
        }

        // 4. 유저 미션 완료 여부 확인 및 배지 지급
        if (userMission.isCompleted()) {
            badgeService.awardBadge(memberId, myPage, badge);
        }
    }

    public void sendMissionNotification(Long memberId, Badge badge, UserMission userMission) {
        String content = null;

        if (badge == Badge.RETROSPECTIVE_MANIA && userMission.getProgressCount() == 1) {
            return;
        } else if (badge == Badge.RETROSPECTIVE_GURU && userMission.getProgressCount() == 1) {
            return;
        }

        if (badge.equals(Badge.KING_OF_COMMUNICATION)) {
            content = "최초로 커뮤니티에 글을 작성했네요!";
        } else if (badge.equals(Badge.RETROSPECTIVE_MANIA)) {
            content = "회고일지를 10회 작성했네요!";
        } else if (badge.equals(Badge.POWER_OF_HEART)) {
            content = "최초로 좋아요를 눌렀네요!";
        } else if (badge.equals(Badge.RETROSPECTIVE_REVIEWER)) {
            content = "최초로 작성한 회고일지를 열람했네요!";
        } else if (badge.equals(Badge.HABIT_STARTER)) {
            content = "최초로 투두리스트를 작성했네요!";
        } else if (badge.equals(Badge.RETROSPECTIVE_GURU)) {
            content = "회고일지를 3회 작성했네요!";
        } else if (badge.equals(Badge.MONTHLY_REPORTER)) {
            content = "최초로 월간 보고서를 열람했네요!";
        } else if (badge.equals(Badge.WEEKLY_REPORTER)) {
            content = "최초로 주간 보고서를 열람했네요!";
        } else if (badge.equals(Badge.POWER_OF_COMMENTS)) {
            content = "최초의 댓글을 작성했네요!";
        } else {
            content = "회고일지를 최초로 공유했네요!";
        }

        notificationService.sendNotification(
                memberId,
                content,
                NotificationType.BADGE,
                "/api/v1/mypage/badges"
        );
    }
}
