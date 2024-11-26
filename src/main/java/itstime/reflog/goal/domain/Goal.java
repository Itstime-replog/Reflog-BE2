package itstime.reflog.goal.domain;

import itstime.reflog.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Goal {

    @Id
    @Column(name = "goal_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(name = "created_date", nullable = false)
    private LocalDate createdDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @PrePersist //엔티티가 데이터베이스에 저장되기 전에 호출되는 메서드 지정
    protected void onCreate() { //엔티티가 데이터베이스에 삽입되기 직전에 호출
        this.createdDate = LocalDate.now();
    }

    public void update(String content, Member member){
        this.content = content;
        this.member = member;
    }
}
