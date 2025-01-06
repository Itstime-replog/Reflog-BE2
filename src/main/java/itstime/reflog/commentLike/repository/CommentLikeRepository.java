package itstime.reflog.commentLike.repository;

import itstime.reflog.comment.domain.Comment;
import itstime.reflog.commentLike.domain.CommentLike;
import itstime.reflog.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);
}
