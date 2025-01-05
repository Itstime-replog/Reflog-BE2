package itstime.reflog.comment.service;

import itstime.reflog.comment.domain.Comment;
import itstime.reflog.comment.dto.CommentDto;
import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberRepository memberRepository;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public void createComment(Long communityId, String memberId, CommentDto.CommentSaveOrUpdateRequest dto) {
        // 1. 멤버 조회
        Member member = memberRepository.findByUuid(UUID.fromString(memberId))
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 2. 커뮤니티 조회
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));

        // 3. parent 댓글 확인
        Comment parentComment = null;
        if (dto.getParentId() != null) {
            parentComment = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._PARENT_COMMENT_NOT_FOUND));
        }

        // 4. 댓글 생성
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .community(community)
                .member(member)
                .parent(parentComment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // 5. 댓글 저장
        commentRepository.save(comment);
    }

    @Transactional
    public void updateComment(Long commentId, CommentDto.CommentSaveOrUpdateRequest dto) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMENT_NOT_FOUND));

        // 2. 댓글 업데이트
        comment.update(dto);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMENT_NOT_FOUND));

        // 2. 댓글 삭제
        commentRepository.delete(comment);
    }
}
