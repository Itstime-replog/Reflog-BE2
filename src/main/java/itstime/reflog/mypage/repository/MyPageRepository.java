package itstime.reflog.mypage.repository;

import itstime.reflog.member.domain.Member;
import itstime.reflog.mypage.domain.MyPage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPageRepository extends JpaRepository<MyPage, Long> {
    Optional<MyPage> findByMember(Member member);
}
