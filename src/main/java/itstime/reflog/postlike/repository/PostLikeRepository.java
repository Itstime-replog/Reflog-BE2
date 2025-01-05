package itstime.reflog.postlike.repository;

import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
import itstime.reflog.postlike.domain.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    PostLike findByMemberAndCommunity (Member member, Community community);
}
