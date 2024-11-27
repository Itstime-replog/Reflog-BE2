package itstime.reflog.goal.repository;

import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyGoalRepository extends JpaRepository<DailyGoal, LocalDate> {

    DailyGoal findByMemberAndCreatedDate(Member member, LocalDate date);

    boolean existsByMemberAndCreatedDate(Member member, LocalDate createdDate);

}
