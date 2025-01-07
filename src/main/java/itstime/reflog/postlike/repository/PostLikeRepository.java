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

    //community 게시물 좋아요 많은 순으로 정렬하고 id, 좋아요 수 반환 : Object[0] = id, Object[1] = 좋아요 수
    //타입을 구분하기 위해 앞에 타입도 반환
    @Query("SELECT 'community', pl.community.id, COUNT(pl) as likeCount FROM PostLike pl GROUP BY pl.community.id " +
            "ORDER BY likeCount DESC")
    List<Object[]> findAllCommunityPostLikesTop();

    //retrospect 게시물 좋아요 많은 순으로 정렬
    @Query("SELECT 'retrospect', pl.retrospect.id, COUNT(pl) as likeCount FROM PostLike pl GROUP BY pl.retrospect.id " +
            "ORDER BY likeCount DESC")
    List<Object[]> findAllRetrospectPostLikesTop();
}
