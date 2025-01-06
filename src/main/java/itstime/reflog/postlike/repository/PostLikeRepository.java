package itstime.reflog.postlike.repository;

import io.lettuce.core.dynamic.annotation.Param;
import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
import itstime.reflog.postlike.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByMemberAndCommunity (Member member, Community community);

    //DB에서 좋아요 합 계산
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.community = :community")
    int countByCommunity(@Param("community") Community community);
}
