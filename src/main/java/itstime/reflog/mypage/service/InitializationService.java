package itstime.reflog.mypage.service;

import itstime.reflog.mission.domain.Badge;
import itstime.reflog.mission.domain.UserBadge;
import itstime.reflog.mission.domain.UserMission;
import itstime.reflog.mission.repository.UserBadgeRepository;
import itstime.reflog.mission.repository.UserMissionRepository;
import itstime.reflog.mypage.domain.MyPage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitializationService {

    private final UserBadgeRepository userBadgeRepository;
    private final UserMissionRepository userMissionRepository;


    @Transactional
    public void initializeForNewMember(MyPage myPage) {

        List<UserBadge> userBadges = Arrays.stream(Badge.values())
                .map(badge -> UserBadge.builder()
                        .myPage(myPage)
                        .badge(badge)
                        .isEarned(false)
                        .build())
                .toList();
        userBadgeRepository.saveAll(userBadges);

        List<UserMission> userMissions = Arrays.stream(Badge.values())
                .map(badge -> UserMission.builder()
                        .myPage(myPage)
                        .badge(badge)
                        .progressCount(0)
                        .build())
                .toList();
        userMissionRepository.saveAll(userMissions);
    }
}
