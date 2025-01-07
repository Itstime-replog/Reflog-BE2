package itstime.reflog.postlike.repository;

import itstime.reflog.postlike.domain.enums.PostType;
import org.springframework.data.repository.query.Param;
import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.retrospect.domain.Retrospect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    //널이 될수 있음
    Optional<PostLike> findByMemberAndCommunity (Member member, Community community);
    Optional<PostLike> findByMemberAndRetrospect (Member member, Retrospect retrospect);

    //DB에서 community 좋아요 합 계산
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.community = :community")
    int countByCommunity(@Param("community") Community community);

    //DB에서 retrospect 좋아요 합 계산
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.retrospect = :retrospect")
    int countByRetrospect(@Param("retrospect") Retrospect retrospect);

    // 내가 좋아요를 누른 모든 커뮤니티 or 회고일지 글
    List<PostLike> findAllByMemberAndPostType(Member member, PostType postType);
}
