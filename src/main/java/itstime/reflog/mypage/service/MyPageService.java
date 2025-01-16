package itstime.reflog.mypage.service;

import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.service.MemberServiceHelper;
import itstime.reflog.mission.domain.UserBadge;
import itstime.reflog.mission.repository.UserBadgeRepository;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.dto.MyPageDto;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.postlike.domain.PostLike;
import itstime.reflog.postlike.domain.enums.PostType;
import itstime.reflog.postlike.repository.PostLikeRepository;
import itstime.reflog.postlike.service.PostLikeService;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static itstime.reflog.mission.domain.Badge.FIRST_MEETING;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final InitializationService initializationService;
    private final MissionService missionService;
    private final CommunityRepository communityRepository;
    private final RetrospectRepository retrospectRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final PostLikeService postLikeService;
    private final MemberServiceHelper memberServiceHelper;
    private final UserBadgeRepository userBadgeRepository;
    private final NotificationSettingsService notificationSettingsService;


    @Transactional
    public MyPageDto.MyPageInfoResponse getMyInformation(String memberId) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 마이페이지 조회
        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        return new MyPageDto.MyPageInfoResponse(
                myPage.getNickname()
        );
    }

    @Transactional
    public void createProfile(String memberId, MyPageDto.MyPageProfileRequest dto) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 닉네임&이메일 중복 확인
        if (myPageRepository.findByNickname(dto.getNickname()).isPresent()) {
            throw new GeneralException(ErrorStatus._DUPLICATE_NICKNAME);
        }
        if (myPageRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new GeneralException(ErrorStatus._DUPLICATE_EMAIL);
        }

        // 3. imageUrl null 확인
        String imageUrl = null;
        if(dto.getImageUrl() == null){
            imageUrl = member.getProfileImageUrl();
        } else{
            imageUrl = dto.getImageUrl();
        }

        // 4. 마이페이지 생성
        MyPage myPage = MyPage.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .imageUrl(imageUrl)
                .member(member)
                .build();

        myPageRepository.save(myPage);
        myPageRepository.flush();

        // 5. 미션세팅 생성
        notificationSettingsService.createDefaultSettings(member);

        // 6. 유저 미션&배지 생성
        initializationService.initializeForNewMember(myPage);

        // 7. 미션
        missionService.incrementMissionProgress(member.getId(), myPage, FIRST_MEETING);
    }

    @Transactional
    public MyPageDto.MyPageProfileResponse getProfile(String memberId) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        return new MyPageDto.MyPageProfileResponse(
                myPage.getNickname(),
                myPage.getEmail(),
                myPage.getImageUrl()
        );
    }

    @Transactional
    public void updateProfile(String memberId, MyPageDto.MyPageProfileRequest dto) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 마이페이지 수정
        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        myPage.update(dto, member);

        myPageRepository.save(myPage);
    }

    @Transactional
    public List<MyPageDto.MyPagePostResponse> getMyPost(String memberId) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 내가 작성한 커뮤니티 글 전체조회
        List<Community> communityList = communityRepository.findAllByMemberOrderByIdDesc(member);

        // 3. 내가 작성한 커뮤니티 글 정리
        List<MyPageDto.MyPagePostResponse> responses = communityList.stream()
                .map(community -> {

                    List<String> postTypes = new ArrayList<>(community.getPostTypes()); // 강제 초기화
                    List<String> learningTypes = new ArrayList<>(community.getLearningTypes()); // 강제 초기화

                    // 좋아요 총 개수
                    int totalLike = postLikeService.getSumCommunityPostLike(community);
                    // 댓글 총 개수
                    long commentCount = commentRepository.countByCommunity(community);

                    return MyPageDto.MyPagePostResponse.fromCommunity(community, postTypes, learningTypes, totalLike, commentCount);
                })
                .collect(Collectors.toList());

        // 4. 내가 작성한 회고일지 글 전체조회
        List<Retrospect> retrospectList = retrospectRepository.findAllByMemberOrderByIdDesc(member);

        // 5. 내가 작성한 회고일지 글 정리
        List<MyPageDto.MyPagePostResponse> retrospectResponses = retrospectList.stream()
                .map(retrospect -> {

                    // 좋아요 총 개수
                    int totalLike = postLikeService.getSumRetrospectPostLike(retrospect);
                    // 댓글 총 개수
                    long commentCount = commentRepository.countByRetrospect(retrospect);

                    return MyPageDto.MyPagePostResponse.fromRetrospect(retrospect, totalLike, commentCount);
                }).toList();

        responses.addAll(retrospectResponses);
        return responses;
    }

    @Transactional
    public List<MyPageDto.MyPagePostResponse> getMyLikePost(String memberId) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 내가 좋아요 한 커뮤니티 글 전체조회
        List<PostLike> postLikeList = postLikeRepository.findAllByMemberAndPostType(member, PostType.COMMUNITY);

        // 3. 내가 좋아요 한 커뮤니티 글 정리
        List<MyPageDto.MyPagePostResponse> responses = postLikeList.stream()
                .map(postLike -> {
                    Community community = postLike.getCommunity();

                    List<String> postTypes = new ArrayList<>(community.getPostTypes()); // 강제 초기화
                    List<String> learningTypes = new ArrayList<>(community.getLearningTypes()); // 강제 초기화

                    // 좋아요 총 개수
                    int totalLike = postLikeService.getSumCommunityPostLike(postLike.getCommunity());
                    // 댓글 총 개수
                    long commentCount = commentRepository.countByCommunity(postLike.getCommunity());

                    return MyPageDto.MyPagePostResponse.fromCommunity(community, postTypes, learningTypes, totalLike, commentCount);
                })
                .collect(Collectors.toList());

        // 4. 내가 좋아요 한 회고일지 글 전체조회
        List<PostLike> postLikeList1 = postLikeRepository.findAllByMemberAndPostType(member, PostType.RETROSPECT);

        // 5. 내가 좋아요 한 회고일지 글 정리
        List<MyPageDto.MyPagePostResponse> retrospectResponses = postLikeList1.stream()
                .map(postLike -> {

                    // 좋아요 총 개수
                    int totalLike = postLikeService.getSumRetrospectPostLike(postLike.getRetrospect());
                    // 댓글 총 개수
                    long commentCount = commentRepository.countByRetrospect(postLike.getRetrospect());

                    return MyPageDto.MyPagePostResponse.fromRetrospect(postLike.getRetrospect(), totalLike, commentCount);
                }).toList();

        responses.addAll(retrospectResponses);
        return responses;
    }

    @Transactional
    public List<MyPageDto.MyPageBadgeResponse> getMyPageBadge(String memberId) {
        // 1. 멤버 조회
        Member member = memberServiceHelper.findMemberByUuid(memberId);

        // 2. 마이페이지 조회
        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        // 3. 유저 미션 조회
        List<UserBadge> userBadges = userBadgeRepository.findByMyPage(myPage);

        return userBadges.stream()
                .map(MyPageDto.MyPageBadgeResponse::fromBadge)
                .collect(Collectors.toList());
    }
}
