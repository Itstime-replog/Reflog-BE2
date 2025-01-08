package itstime.reflog.mission.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserBadge;
import itstime.reflog.mission.repository.UserBadgeRepository;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.notification.domain.NotificationType;
import itstime.reflog.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final NotificationService notificationService;

    @Transactional
    public void awardBadge(Long memberId, MyPage myPage, Badge badge) {
        // 1. 유저 배지 조회
        UserBadge userBadge = userBadgeRepository.findByMyPageAndBadge(myPage, badge)
                .orElseThrow(() -> new GeneralException(ErrorStatus._BADGE_NOT_FOUND));

        // 2. 이미 배지를 획득했으면 반환
        if (userBadge.isEarned()) {
            return;
        }

        // 3. 유저 배지 획득 처리
        userBadge.setEarned(true);
        userBadgeRepository.save(userBadge);

        notificationService.sendNotification(
                memberId,
                badge.getKoreanName() + " 배지를 획득했어요!",
                NotificationType.BADGE
        );
    }
}
