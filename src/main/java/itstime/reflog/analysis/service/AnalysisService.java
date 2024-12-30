package itstime.reflog.analysis.service;

import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final MemberRepository memberRepository;
    private final WeeklyAnalysisService weeklyAnalysisService;
    private final MonthlyAnalysisService monthlyAnalysisService;

    @Transactional
    public void runWeeklyAnalysis() {
        List<Member> members = memberRepository.findAll();
        members.forEach(member -> weeklyAnalysisService.createWeeklyAnalysis(member.getId()));
    }

    @Transactional
    public void runMonthlyAnalysis() {
        List<Member> members = memberRepository.findAll();
        members.forEach(member -> monthlyAnalysisService.createMonthlyAnalysis(member.getId()));
    }
}
