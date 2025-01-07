package itstime.reflog.goal.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.goal.dto.DailyGoalDto;
import itstime.reflog.goal.repository.DailyGoalRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyGoalService {
    private final DailyGoalRepository dailyGoalRepository;
    private final MemberServiceHelper memberServiceHelper;


    @Transactional
    public void createDailyGoal(String memberId, DailyGoalDto.DailyGoalSaveOrUpdateRequest dto){
        Member member = memberServiceHelper.findMemberByUuid(memberId);

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
    public DailyGoalDto.DailyGoalResponse getDailyGoalByMemberIdAndDate(String memberId, LocalDate date){
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        DailyGoal dailyGoal = dailyGoalRepository.findByMemberAndCreatedDate(member, date);

        return new DailyGoalDto.DailyGoalResponse(
                dailyGoal.getCreatedDate(), // getCreatedDate()로 수정, id를 localdate로 변경
                dailyGoal.getContent()
        );

    }

    @Transactional
    public void updateDailyGoal(LocalDate createdDate, String memberId, DailyGoalDto.DailyGoalSaveOrUpdateRequest request){
        DailyGoal dailyGoal = dailyGoalRepository.findById(createdDate)
                .orElseThrow(() -> new GeneralException((ErrorStatus._DAILY_GOAL_NOT_FOUND)));

        Member member = memberServiceHelper.findMemberByUuid(memberId);

        dailyGoal.update(request.getContent(), member);

        dailyGoalRepository.save(dailyGoal);
    }
}
