package itstime.reflog.commentLike.repository;

import itstime.reflog.commentLike.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
}
