package itstime.reflog.comment.domain;

import itstime.reflog.comment.dto.CommentDto;
import itstime.reflog.commentLike.domain.CommentLike;
import itstime.reflog.community.domain.Community;
import itstime.reflog.member.domain.Member;
import itstime.reflog.retrospect.domain.Retrospect;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Comment {

    @Id
    @Column(name = "comment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "commnunity_id", nullable = true)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospect_id", nullable = true)
    private Retrospect retrospect;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentLike> likes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    private LocalDateTime createdAt; // 생성일

    private LocalDateTime updatedAt; // 수정일

    public void update(CommentDto.CommentSaveOrUpdateRequest dto) {
        this.content = dto.getContent();
        this.updatedAt = LocalDateTime.now();
    }
}
