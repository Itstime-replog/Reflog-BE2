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

import java.time.LocalDate;
import java.util.Date;
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

        // 해당 날짜에 이미 목표가 존재하는지 확인
        LocalDate today = LocalDate.now();
        boolean exists = dailyGoalRepository.existsByMemberAndCreatedDate(member, today);

        if (exists) {
            throw new GeneralException(ErrorStatus._DAILY_GOAL_ALREADY_EXISTS);  // 이미 목표가 존재하는 경우 예외 처리
        }

        DailyGoal dailyGoal = DailyGoal.builder()
                .content(dto.getContent())
                .member(member)
                .build();
        dailyGoalRepository.save(dailyGoal);
    }

    @Transactional
    public DailyGoalDTO.DailyGoalResponse getDailyGoalByMemberIdAndDate(Long memberId, LocalDate date){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        DailyGoal dailyGoal = dailyGoalRepository.findByMemberAndCreatedDate(member, date);

        return new DailyGoalDTO.DailyGoalResponse(
                dailyGoal.getCreatedDate(), // getCreatedDate()로 수정, id를 localdate로 변경
                dailyGoal.getContent()
        );

    }

    @Transactional
    public void updateDailyGoal(LocalDate createdDate, Long memberId, DailyGoalDTO.DailyGoalSaveOrUpdateRequest request){
        DailyGoal dailyGoal = dailyGoalRepository.findById(createdDate)
                .orElseThrow(() -> new GeneralException((ErrorStatus._DAILY_GOAL_NOT_FOUND)));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        dailyGoal.update(request.getContent(), member);

        dailyGoalRepository.save(dailyGoal);
    }
}
