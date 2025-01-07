package itstime.reflog.comment.service;

import itstime.reflog.comment.domain.Comment;
import itstime.reflog.comment.dto.CommentDto;
import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.commentLike.domain.CommentLike;
import itstime.reflog.commentLike.repository.CommentLikeRepository;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommunityRepository communityRepository;
    private final RetrospectRepository retrospectRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberServiceHelper memberServiceHelper;


    @Transactional
    public void createComment(Long postId, String memberId, CommentDto.CommentSaveOrUpdateRequest dto) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 게시물 조회
        PostType postType = PostType.valueOf(dto.getPostType());
        Community community = null;
        Retrospect retrospect = null;

        if (postType == PostType.COMMUNITY) {
            community = communityRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));
        } else if (postType == PostType.RETROSPECT) {
            retrospect = retrospectRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._RETROSPECT_NOT_FOUND));
        } else {
            throw new GeneralException(ErrorStatus._INVALID_POST_TYPE);
        }

        // 3. parent 댓글 확인
        Comment parentComment = null;
        if (dto.getParentId() != null) {
            parentComment = commentRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new GeneralException(ErrorStatus._PARENT_COMMENT_NOT_FOUND));
        }

        // 4. 댓글 생성
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .postType(postType)
                .member(member)
                .parent(parentComment)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        if (postType == PostType.COMMUNITY) {
            comment.setCommunity(community);
        } else {
            comment.setRetrospect(retrospect);
        }

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

    @Transactional
    public void toggleCommentLike(Long commentId,  String memberId) {
        // 1. 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._COMMENT_NOT_FOUND));

        // 2. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 3. 좋아요 상태 확인
        Optional<CommentLike> optionalCommentLike = commentLikeRepository.findByCommentAndMember(comment, member);

        if (optionalCommentLike.isPresent()) {
            // 좋아요가 이미 존재하면 상태 토글
            CommentLike commentLike = optionalCommentLike.get();
            commentLike.update(!commentLike.isLiked());
        } else {
            // 좋아요가 없으면 새로 생성
            CommentLike newLike = CommentLike.builder()
                    .comment(comment)
                    .member(member)
                    .isLiked(true)
                    .build();

            commentLikeRepository.save(newLike);
        }

    }
}
