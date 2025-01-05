package itstime.reflog.community.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.community.domain.Community;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT DISTINCT c FROM Community c JOIN c.learningTypes lt JOIN c.postTypes pt " +
            "WHERE (:learningTypes IS NULL AND :postTypes IS NULL) OR (lt IN :learningTypes OR pt IN :postTypes)")
    List<Community> findByLearningTypesAndPostTypes(
            @Param("postTypes") List<String> postTypes, @Param("learningTypes") List<String> learningTypes);

    //기타는 기타:%s에 해당하는 모든 커뮤니티 반환
    @Query("SELECT DISTINCT c FROM Community c JOIN c.learningTypes lt JOIN c.postTypes pt WHERE (:typePrefix IS NULL AND :postTypes IS NULL AND :learningType IS NULL) " +
            "OR (lt LIKE CONCAT(:typePrefix, '%') OR pt IN :postTypes OR lt = :learningType)")
    List<Community> findCommunitiesByLearningTypePrefix(@Param("postTypes") List<String> postTypes, @Param("typePrefix") String typePrefix, @Param("learningType") String learningType);

    @Query("SELECT c FROM Community c WHERE c.title LIKE %:title%")
    List<Community> searchCommunitiesByTitleContaining(@Param("title") String title);
}
