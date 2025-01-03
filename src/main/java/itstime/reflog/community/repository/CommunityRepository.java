package itstime.reflog.community.repository;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import itstime.reflog.community.domain.Community;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT DISTINCT c FROM Community c JOIN c.learningTypes lt JOIN c.postTypes pt " +
            "WHERE (:learningTypes IS NULL OR lt IN :learningTypes) AND (:postTypes IS NULL OR pt IN :postTypes)")
    List<Community> findByLearningTypesAndPostTypes(
            @Param("postTypes") List<String> postTypes, @Param("learningTypes") List<String> learningTypes);
}
