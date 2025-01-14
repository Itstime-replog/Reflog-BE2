package itstime.reflog.postlike.service;

import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.dto.CommunityDto;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.notification.domain.NotificationType;
import itstime.reflog.notification.service.NotificationService;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.domain.enums.LikeType;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.postlike.dto.PostLikeDto;
import itstime.reflog.postlike.repository.PostLikeRepository;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static itstime.reflog.mission.domain.Badge.POWER_OF_HEART;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final CommunityRepository communityRepository;
    private final RetrospectRepository retrospectRepository;
    private final MyPageRepository myPageRepository;
    private final MemberServiceHelper memberServiceHelper;
    private final MissionService missionService;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    private final MemberRepository memberRepository;

    @Transactional
    public void togglePostLike(String memberId, Long postId, PostLikeDto.PostLikeSaveRequest dto){
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        PostLike postLike;

        //1. 글 유형에 따라 다른 엔티티에서 테이블 가져오기
        if (PostType.COMMUNITY == PostType.valueOf(dto.getPostType())) {
            Community community = communityRepository.findById(postId)
                    .orElseThrow(() -> new GeneralException(ErrorStatus._COMMUNITY_NOT_FOUND));

            //좋아요인 경우
            if (LikeType.LIKE == LikeType.valueOf(dto.getLikeType())) {
                //커뮤니티, 멤버 id와 일치하는 좋아요 가져오기
                postLike = postLikeRepository.findLikeByMemberAndCommunity(member, community)
                        .orElse(null);

                //2.좋아요 존재 여부 확인
                if (postLike != null && postLike.getLikeType().equals(LikeType.LIKE)) {
                    //좋아요가 이미 있다면 테이블 삭제
                    postLikeRepository.delete(postLike);
                } else {
                    //좋아요가 없다면 테이블 생성
                    PostLike newPostLike = PostLike.builder()
                            .member(member)
                            .community(community)
                            .postType(PostType.COMMUNITY)
                            .likeType(LikeType.LIKE) //좋아요
                            .build();

                    postLikeRepository.save(newPostLike);

                    // 미션
                    missionService.incrementMissionProgress(member.getId(), myPage, POWER_OF_HEART);

                    // 알림
                    sendCommunityLikeNotification(community, member);
                }

            } else if (LikeType.BOOKMARK == LikeType.valueOf(dto.getLikeType())) {
                //커뮤니티, 멤버 id와 일치하는 북마크 가져오기
                postLike = postLikeRepository.findBookmarkByMemberAndCommunity(member, community)
                        .orElse(null);

                if (postLike != null && postLike.getLikeType().equals(LikeType.BOOKMARK)) {
                    //북마크가 이미 있다면 테이블 삭제
                    postLikeRepository.delete(postLike);
                } else {
                    //북마크가 없다면 테이블 생성
                    PostLike newPostLike = PostLike.builder()
                            .member(member)
                            .community(community)
                            .postType(PostType.COMMUNITY)
                            .likeType(LikeType.BOOKMARK) //북마크
                            .build();

                    postLikeRepository.save(newPostLike);

                }
            } else {
                throw new GeneralException(ErrorStatus._POSTLIKE_LIKETYPE_BAD_REQUEST);
            }
        }
        else if (PostType.RETROSPECT == PostType.valueOf(dto.getPostType())){
            Retrospect retrospect = retrospectRepository.findById(postId)
                    .orElseThrow(()-> new GeneralException(ErrorStatus._RETROSPECT_NOT_FOUND));

            if (LikeType.LIKE == LikeType.valueOf(dto.getLikeType())) {

                //회고일지, 멤버 id와 일치하는 좋아요 가져오기
                postLike = postLikeRepository.findLikeByMemberAndRetrospect(member, retrospect)
                        .orElse(null);

                //2.좋아요 존재 여부 확인
                if (postLike != null && postLike.getLikeType().equals(LikeType.LIKE)) {
                    //좋아요가 이미 있다면 테이블 삭제
                    postLikeRepository.delete(postLike);
                } else {
                    //좋아요가 없다면 테이블 생성
                    PostLike newPostLike = PostLike.builder()
                            .member(member)
                            .retrospect(retrospect)
                            .postType(PostType.RETROSPECT)
                            .likeType(LikeType.LIKE)
                            .build();

                    postLikeRepository.save(newPostLike);

                    // 미션
                    missionService.incrementMissionProgress(member.getId(), myPage, POWER_OF_HEART);

                    // 알림
                    sendRetrospectLikeNotification(retrospect, member);


                }
            } else if (LikeType.BOOKMARK == LikeType.valueOf(dto.getLikeType())) { //북마크한경우
                //커뮤니티, 멤버 id와 일치하는 북마크 가져오기
                postLike = postLikeRepository.findBookmarkByMemberAndRetrospect(member, retrospect)
                        .orElse(null);

                if (postLike != null && postLike.getLikeType().equals(LikeType.BOOKMARK)) {
                    //북마크가 이미 있다면 테이블 삭제
                    postLikeRepository.delete(postLike);
                } else {
                    //좋아요가 없다면 테이블 생성
                    PostLike newPostLike = PostLike.builder()
                            .member(member)
                            .retrospect(retrospect)
                            .postType(PostType.RETROSPECT)
                            .likeType(LikeType.BOOKMARK) //북마크
                            .build();

                    postLikeRepository.save(newPostLike);

                }
            } else {
                throw new GeneralException(ErrorStatus._POSTLIKE_LIKETYPE_BAD_REQUEST);
            }
        }
        //3. enum으로 설정하지 않은 글 유형이 올 경우 에러 출력
        else {
            throw new GeneralException(ErrorStatus._POSTLIKE_BAD_REQUEST);
        }

    }

    //게시물마다 좋아요 갯수 항상 0이상이므로 int로
    @Transactional
    public int getSumCommunityPostLike(Community community) {
        return postLikeRepository.countByCommunity(community);
    }

    @Transactional
    public int getSumRetrospectPostLike(Retrospect retrospect) {
        return postLikeRepository.countByRetrospect(retrospect);
    }

    @Transactional
    public List<CommunityDto.CombinedCategoryResponse> getTopLikeCommunityPosts(String memberId) {
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        //2. 커뮤니티, 회고일지 좋아요 순으로 각각 가져와서 하나의 배열에 저장
        List<Object[]> postLikesTop = new ArrayList<>(postLikeRepository.findCommunityByPostLikeTop());
        postLikesTop.addAll(postLikeRepository.findARetrospectPostLikesTop());

        //좋아요 수인 object[1]이 Object 타입이기 떄문에 Long 타입으로 바꿔 준 후 비교 정렬
        postLikesTop.sort((o1, o2) -> Long.compare((Long) o2[2], (Long) o1[2]));

        //이 중 상위 세개만 가져옴
        List<Object[]> postLikesTopThree = postLikesTop.stream().limit(3).toList();

        return postLikesTopThree.stream()
                .map(popularPost -> {
                    if (popularPost[0] == PostType.COMMUNITY) {
                        Community community = communityRepository.findById((Long)popularPost[1])
                                .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
                        String nickname = myPageRepository.findByMember(community.getMember())
                                .map(MyPage::getNickname)
                                .orElse("닉네임 없음");
                        Boolean isLike = postLikeRepository.findLikeByMemberAndCommunity(member, community).isPresent();
                        int totalLike = getSumCommunityPostLike(community);

                        //게시물마다 댓글 수 반환
                        Long totalComment = commentRepository.countByCommunity(community);

                        return CommunityDto.CombinedCategoryResponse.fromCommunity(community, nickname, isLike, totalLike, totalComment);

                    } else if (popularPost[0] == PostType.RETROSPECT) {
                        Retrospect retrospect = retrospectRepository.findById((Long)popularPost[1])
                                .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
                        String nickname = myPageRepository.findByMember(retrospect.getMember())
                                .map(MyPage::getNickname)
                                .orElse("닉네임 없음");
                        Boolean isLike = postLikeRepository.findLikeByMemberAndRetrospect(member, retrospect).isPresent();
                        int totalLike = getSumRetrospectPostLike(retrospect);

                        //게시물마다 댓글 수 반환
                        Long totalComment = commentRepository.countByRetrospect(retrospect);

                        return CommunityDto.CombinedCategoryResponse.fromRetrospect(retrospect, nickname, isLike, totalLike, totalComment);

                    } else {
                        throw new GeneralException(ErrorStatus._POST_NOT_FOUND);
                    }
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<PostLikeDto.BookMarkResponse> getBookmarks(String memberId){
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        List<PostLike> postLikes = postLikeRepository.findPostLikesByMember(member);

        return postLikes.stream()
                .map(postLike -> {

                    //커뮤니티 타입인 경우 커뮤니티 응답
                    if (postLike.getPostType() == PostType.COMMUNITY) {
                        Community community = communityRepository.findById(postLike.getCommunity().getId())
                                .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
                        return PostLikeDto.BookMarkResponse.fromCommunityEntity(community);

                    } else if (postLike.getPostType() == PostType.RETROSPECT) {
                        Retrospect retrospect = retrospectRepository.findById(postLike.getRetrospect().getId())
                                .orElseThrow(() -> new GeneralException(ErrorStatus._POST_NOT_FOUND));
                        return PostLikeDto.BookMarkResponse.fromRetrospectEntity(retrospect);
                    }
                    throw new GeneralException(ErrorStatus._POSTLIKE_BAD_REQUEST);
                })
                .collect(Collectors.toList());
    }


    public void sendCommunityLikeNotification(Community community, Member sender) {
        Member receiver = community.getMember(); // 알림 받는 자

        String title = community.getTitle(); // 글 제목

        String nickname = sender.getMyPage().getNickname(); // 좋아요 누른 자

        notificationService.sendNotification(
                receiver.getId(),
                nickname + " 님이 " + title + "에 좋아요를 눌렀습니다.",
                NotificationType.COMMUNITY,
                "/api/v1/communities/" + community.getId()
        );
    }

    public void sendRetrospectLikeNotification(Retrospect retrospect, Member sender) {
        Member receiver = retrospect.getMember(); // 알림 받는 자

        String title = retrospect.getTitle(); // 글 제목

        String nickname = sender.getMyPage().getNickname(); // 좋아요 누른 자

        notificationService.sendNotification(
                receiver.getId(),
                nickname + " 님이 " + title + "에 좋아요를 눌렀습니다.",
                NotificationType.COMMUNITY,
                "/api/v1/retrospect/?retrospectId=" + retrospect.getId()
        );
    }
}
