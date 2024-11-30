package itstime.reflog.schedule.repository;

import itstime.reflog.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.member.id = :memberId AND MONTH(s.startDateTime) = :month")
    List<Schedule> findByMemberAndStartDateTimeMonth(@Param("memberId") Long memberId, @Param("month") int month);
}
