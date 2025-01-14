package itstime.reflog.mypage.domain;

import itstime.reflog.member.domain.Member;
import itstime.reflog.mission.domain.UserBadge;
import itstime.reflog.mission.domain.UserMission;
import itstime.reflog.mypage.dto.MyPageDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

    private String imageUrl;

    @OneToMany(mappedBy = "myPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBadge> userBadges;

    @OneToMany(mappedBy = "myPage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserMission> userMissions;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public void update(MyPageDto.MyPageProfileRequest dto, Member member) {
        this.nickname = dto.getNickname();
        this.email = dto.getEmail();
        this.imageUrl = dto.getImageUrl();
        this.member = member;
    }
}
