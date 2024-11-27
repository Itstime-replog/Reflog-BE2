package itstime.reflog.goal.repository;

import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {

    List<DailyGoal> findByMemberAndCreatedDate(Member member, LocalDate date);
}
