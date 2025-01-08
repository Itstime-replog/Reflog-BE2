package itstime.reflog.comment.service;

import itstime.reflog.comment.domain.Comment;
import itstime.reflog.comment.dto.CommentDto;
import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.commentlike.domain.CommentLike;
import itstime.reflog.commentlike.repository.CommentLikeRepository;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.notification.domain.NotificationType;
import itstime.reflog.notification.service.NotificationService;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static itstime.reflog.mission.domain.Badge.POWER_OF_COMMENTS;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommunityRepository communityRepository;
    private final RetrospectRepository retrospectRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final MemberServiceHelper memberServiceHelper;
    private final MissionService missionService;
    private final MyPageRepository myPageRepository;
    private final NotificationService notificationService;


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

        // 6. 미션
        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        missionService.incrementMissionProgress(member.getId(), myPage, POWER_OF_COMMENTS);

        // 7. 알림
        if (retrospect == null) {
            assert community != null;
            sendCommunityCommentNotification(community, member);
        }else{
            sendRetrospectCommentNotification(retrospect, member);
        }
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
    public void toggleCommentLike(Long commentId, String memberId) {
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

    public void sendCommunityCommentNotification(Community community, Member sender) {
        Member receiver = community.getMember(); // 알림 받는 자

        String title = community.getTitle(); // 글 제목

        String nickname = sender.getMyPage().getNickname(); // 좋아요 누른 자

        notificationService.sendNotification(
                receiver.getId(),
                nickname + " 님이 " + title + "에 댓글을 남겼습니다.",
                NotificationType.POSTLIKE
        );
    }

    public void sendRetrospectCommentNotification(Retrospect retrospect, Member sender) {
        Member receiver = retrospect.getMember(); // 알림 받는 자

        String title = retrospect.getTitle(); // 글 제목

        String nickname = sender.getMyPage().getNickname(); // 좋아요 누른 자

        notificationService.sendNotification(
                receiver.getId(),
                nickname + " 님이 " + title + "에 댓글을 남겼습니다.",
                NotificationType.COMMENT
        );
    }
}
