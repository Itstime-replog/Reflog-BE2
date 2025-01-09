package itstime.reflog.postlike.repository;

import itstime.reflog.postlike.domain.PopularPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PopularPostRepository extends JpaRepository<PopularPost, Long> {
}
