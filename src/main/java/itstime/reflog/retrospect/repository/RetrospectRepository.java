package itstime.reflog.retrospect.repository;

import java.time.LocalDate;
import java.util.Optional;

import itstime.reflog.member.domain.Member;
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

	//제목에 키워드를 포함하고 공개로 설정된 회고일지 찾기
	@Query("SELECT r FROM Retrospect r WHERE r.visibility = true AND r.title LIKE %:title%")
	List<Retrospect> findByTitleContainingAndVisibilityIsTrue(@Param("title") String title);

	//공개로 설정한 모든 회고일지 생성날짜 기준 정렬
	List<Retrospect> findAllByVisibilityTrueOrderByCreatedDateDesc();

	//내가 작성한 글 모두 찾기
	List<Retrospect> findAllByMemberOrderByIdDesc(Member member);
}
