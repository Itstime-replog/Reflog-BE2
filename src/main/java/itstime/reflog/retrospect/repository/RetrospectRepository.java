package itstime.reflog.retrospect.repository;

import java.time.LocalDate;
import java.util.Optional;

import itstime.reflog.community.domain.Community;
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

	List<Retrospect> findByVisibilityIsTrue();
  
	//회고일지에 studytype을 조인해 파라미터로 주어진 type과 동일한 회고일지 반환
	@Query("SELECT r FROM Retrospect r JOIN r.studyTypes st WHERE st.type = :type AND r.member = :member")
	List<Retrospect> findRetrospectsByTypeAndMember(@Param("type") String type, @Param("member") Member member);

	//기타는 기타:%s에 해당하는 모든 회고일지 반환
	@Query("SELECT r FROM Retrospect r JOIN r.studyTypes st WHERE st.type LIKE :typePrefix AND r.member = :member")
	List<Retrospect> findRetrospectsByTypePrefixAndMember(@Param("typePrefix") String typePrefix, @Param("member") Member member);

	//검색 API
	@Query("SELECT r FROM Retrospect r WHERE r.visibility = true AND r.title LIKE %:title%")
	List<Retrospect> findByTitleContainingAndVisibilityIsTrue(@Param("title") String title);
}
