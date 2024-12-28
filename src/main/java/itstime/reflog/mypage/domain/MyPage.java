package itstime.reflog.mypage.domain;

import itstime.reflog.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MyPage {

    @Id
    @Column(name = "mypage_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
