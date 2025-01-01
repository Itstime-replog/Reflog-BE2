package itstime.reflog.community.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.community.domain.Community;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT c FROM Community c WHERE (COALESCE(:learningTypes, NULL) IS NULL OR c.learningTypes IN :learningTypes) " +
            "AND (COALESCE(:postTypes, NULL) IS NULL OR c.postTypes IN :postTypes)")
    List<Community> findByLearningTypesAndPostTypes(
            @Param("learningTypes") List<String> learningTypes, @Param("postTypes") List<String> postTypes);
}
