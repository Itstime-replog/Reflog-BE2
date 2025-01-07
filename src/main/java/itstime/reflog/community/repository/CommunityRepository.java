package itstime.reflog.community.repository;

import io.lettuce.core.dynamic.annotation.Param;
import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT DISTINCT c FROM Community c JOIN c.learningTypes lt JOIN c.postTypes pt " +
            "WHERE (:learningTypes IS NULL AND :postTypes IS NULL) OR (lt IN :learningTypes OR pt IN :postTypes)")
    List<Community> findByLearningTypesAndPostTypes(
            @Param("postTypes") List<String> postTypes, @Param("learningTypes") List<String> learningTypes);

    //기타는 기타:%s에 해당하는 모든 커뮤니티 반환
    @Query("SELECT DISTINCT c FROM Community c JOIN c.learningTypes lt JOIN c.postTypes pt WHERE (:typePrefix IS NULL AND :postTypes IS NULL AND :learningType IS NULL) " +
            "OR (CAST(lt AS string) LIKE CONCAT(:typePrefix, '%') OR pt IN :postTypes OR lt = :learningType)")
    List<Community> findCommunitiesByLearningTypePrefix(@Param("postTypes") List<String> postTypes, @Param("typePrefix") String typePrefix, @Param("learningType") String learningType);

    //제목에 키워드를 포함하고 있는 게시물 찾기
    @Query("SELECT c FROM Community c WHERE c.title LIKE %:title%")
    List<Community> searchCommunitiesByTitleContaining(@Param("title") String title);

    //내가 작성한 글 모두 찾기
    List<Community> findAllByMemberOrderByIdDesc(Member member);
}
