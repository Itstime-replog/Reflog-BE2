package itstime.reflog.retrospect.repository;

import java.time.LocalDate;
import java.util.Optional;
import itstime.reflog.member.domain.Member;
import itstime.reflog.retrospect.domain.StudyType;
import itstime.reflog.todolist.domain.Todolist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import itstime.reflog.retrospect.domain.Retrospect;

import java.util.List;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {

	@Query("SELECT MAX(r.createdDate) FROM Retrospect r WHERE r.member.id = :memberId")
	Optional<LocalDate> findLatestRetrospectDateByMemberId(@Param("memberId") Long memberId);

    List<Retrospect> findByMember(Member member);

	@Query("SELECT r FROM Retrospect r JOIN r.studyTypes st WHERE st.type = :type AND r.member = :member")
	List<Retrospect> findRetrospectsByTypeAndMember(@Param("type") String type, @Param("member") Member member);}
