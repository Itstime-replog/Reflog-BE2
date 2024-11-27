package itstime.reflog.goal.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.goal.dto.DailyGoalDTO;
import itstime.reflog.goal.repository.DailyGoalRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyGoalService {
    private final DailyGoalRepository dailyGoalRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void createDailyGoal(Long memberId,DailyGoalDTO.DailyGoalSaveOrUpdateRequest dto){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()-> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        DailyGoal dailyGoal = DailyGoal.builder()
                .content(dto.getContent())
                .member(member)
                .build();
        dailyGoalRepository.save(dailyGoal);
    }
}
