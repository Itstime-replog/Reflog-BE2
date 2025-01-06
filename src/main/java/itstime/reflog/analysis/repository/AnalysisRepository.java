package itstime.reflog.analysis.repository;

import itstime.reflog.analysis.domain.WeeklyAnalysis;
import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AnalysisRepository extends JpaRepository<WeeklyAnalysis, Long> {

    WeeklyAnalysis findByMemberAndStartDate(Member member, LocalDate date);

}
