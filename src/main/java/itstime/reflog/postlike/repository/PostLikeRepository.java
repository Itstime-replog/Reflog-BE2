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

    //좋아요 가져오기,,, 널이 될수 있음
    @Query("SELECT pl from PostLike pl WHERE pl.likeType = 'LIKE'")
    Optional<PostLike> findLikeByMemberAndCommunity (Member member, Community community);

    @Query("SELECT pl from PostLike pl WHERE pl.likeType = 'LIKE'")
    Optional<PostLike> findLikeByMemberAndRetrospect (Member member, Retrospect retrospect);

    //북마크 가져오기,, 널이 될수있음
    @Query("SELECT pl from PostLike pl WHERE pl.likeType = 'BOOKMARK'")
    Optional<PostLike> findBookmarkByMemberAndCommunity (Member member, Community community);

    @Query("SELECT pl from PostLike pl WHERE pl.likeType = 'BOOKMARK'")
    Optional<PostLike> findBookmarkByMemberAndRetrospect (Member member, Retrospect retrospect);

    //DB에서 community 좋아요 합 계산
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.community = :community AND pl.likeType = 'LIKE'")
    int countByCommunity(@Param("community") Community community);

    //DB에서 retrospect 좋아요 합 계산
    @Query("SELECT COUNT(pl) FROM PostLike pl WHERE pl.retrospect = :retrospect AND pl.likeType = 'LIKE'")
    int countByRetrospect(@Param("retrospect") Retrospect retrospect);

    // 내가 좋아요를 누른 모든 커뮤니티 or 회고일지 글
    List<PostLike> findAllByMemberAndPostType(Member member, PostType postType);

    //community 게시물 좋아요 많은 순으로 정렬하고 id, 좋아요 수 반환 : Object[0] = id, Object[1] = 좋아요 수
    //타입을 구분하기 위해 앞에 타입도 반환
    @Query("SELECT 'COMMUNITY', pl.community.id, COUNT(pl) as likeCount FROM PostLike pl WHERE pl.postType = 'COMMUNITY' AND pl.likeType = 'LIKE' GROUP BY pl.community.id " +
            "ORDER BY likeCount DESC")
    List<Object[]> findCommunityByPostLikeTop();

    //retrospect 게시물 좋아요 많은 순으로 정렬
    @Query("SELECT 'RETROSPECT', pl.retrospect.id, COUNT(pl) as likeCount FROM PostLike pl WHERE pl.postType = 'RETROSPECT' AND pl.likeType = 'LIKE' GROUP BY pl.retrospect.id " +
            "ORDER BY likeCount DESC")
    List<Object[]> findARetrospectPostLikesTop();
}
