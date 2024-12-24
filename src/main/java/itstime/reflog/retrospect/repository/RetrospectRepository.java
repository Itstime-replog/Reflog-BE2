package itstime.reflog.retrospect.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import itstime.reflog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import io.lettuce.core.dynamic.annotation.Param;
import itstime.reflog.retrospect.domain.Retrospect;

public interface RetrospectRepository extends JpaRepository<Retrospect, Long> {
	@Query("SELECT MAX(r.createdDate) FROM Retrospect r WHERE r.member.id = :memberId")
	Optional<LocalDate> findLatestRetrospectDateByMemberId(@Param("memberId") Long memberId);

	List<Retrospect> findByMember(Member member);

}
