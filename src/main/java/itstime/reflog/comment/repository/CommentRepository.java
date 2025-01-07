package itstime.reflog.comment.repository;

import itstime.reflog.comment.domain.Comment;
import itstime.reflog.community.domain.Community;
import itstime.reflog.retrospect.domain.Retrospect;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 커뮤니티 모든 댓글 조회
    List<Comment> findAllByCommunityOrderByCreatedAtDesc(Community community);
    // 회고일지 모든 댓글 조회
    List<Comment> findAllByRetrospectOrderByCreatedAtDesc(Retrospect retrospect);

    // 커뮤니티 댓글 개수 조회
    long countByCommunity(Community community);

    // 회고일지 댓글 개수 조회
    long countByRetrospect(Retrospect retrospect);
}
