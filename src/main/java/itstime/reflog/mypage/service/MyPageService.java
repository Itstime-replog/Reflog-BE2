package itstime.reflog.mypage.service;

import itstime.reflog.comment.repository.CommentRepository;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.community.domain.Community;
import itstime.reflog.community.dto.CommunityDto;
import itstime.reflog.community.repository.CommunityRepository;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.dto.MyPageDto;
import itstime.reflog.mypage.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static itstime.reflog.mission.domain.Badge.FIRST_MEETING;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final MemberRepository memberRepository;
    private final InitializationService initializationService;
    private final MissionService missionService;
    private final CommunityRepository communityRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public MyPageDto.MyPageInfoResponse getMyInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        return new MyPageDto.MyPageInfoResponse(
                myPage.getNickname()
        );
    }

    @Transactional
    public void createProfile(Long memberId, MyPageDto.MyPageProfileRequest dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MyPage myPage = MyPage.builder()
                .nickname(dto.getNickname())
                .email(dto.getEmail())
                .member(member)
                .build();

        myPageRepository.save(myPage);
        myPageRepository.flush();

        initializationService.initializeForNewMember(myPage);

        missionService.incrementMissionProgress(memberId, FIRST_MEETING);
    }

    @Transactional
    public MyPageDto.MyPageProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        return new MyPageDto.MyPageProfileResponse(
                myPage.getNickname(),
                myPage.getEmail()
        );
    }

    @Transactional
    public void updateProfile(Long memberId, MyPageDto.MyPageProfileRequest dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MyPage myPage = myPageRepository.findByMember(member)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        myPage.update(dto, member);

        myPageRepository.save(myPage);
    }

    @Transactional
    public MyPageDto.MyPagePostResponse getMyPost(String memberId) {
        // 1. 멤버 조회
        Member member = memberRepository.findByUuid(UUID.fromString(memberId))
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        // 2. 내가 작성한 글 전체조회
        List<Community> communityList = communityRepository.findAllByMemberOrderByIdDesc(member);

        // 3. 내가 작성한 글 정리
        List<CommunityDto.MyPageCommunityResponse> myPageCommunityResponseList = communityList.stream()
                .map(community -> new CommunityDto.MyPageCommunityResponse(
                        community.getId(),
                        community.getTitle(),
                        community.getContent(),
                        community.getCreatedAt(),
                        commentRepository.countByCommunity(community)
                ))
                .collect(Collectors.toList());

        return new MyPageDto.MyPagePostResponse(myPageCommunityResponseList);
    }
}
