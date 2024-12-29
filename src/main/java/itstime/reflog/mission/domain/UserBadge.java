package itstime.reflog.mission.domain;

import itstime.reflog.mypage.domain.MyPage;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class UserBadge {

    @Id
    @Column(name = "user_badge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private boolean isEarned;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mypage_id", nullable = false)
    private MyPage myPage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Badge badge;

    public void setEarned(boolean b) {
        this.isEarned = b;
    }
}
