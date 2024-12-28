package itstime.reflog.mypage.repository;

import itstime.reflog.member.domain.Member;
import itstime.reflog.mypage.domain.MyPage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyPageRepository extends JpaRepository<MyPage, Long> {
    boolean existsByMember(Member member);

}
