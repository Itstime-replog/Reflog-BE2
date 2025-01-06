package itstime.reflog.postlike.domain;

import itstime.reflog.analysis.domain.enums.Period;
import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.retrospect.domain.Retrospect;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = true) //널 가능
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospect_id", nullable = true) //널 가능
    private Retrospect retrospect;

    @Enumerated(EnumType.STRING)
    private PostType postType;

}
