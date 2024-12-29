package itstime.reflog.mypage.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.mission.service.MissionService;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.dto.MyPageDto;
import itstime.reflog.mypage.repository.MyPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static itstime.reflog.mission.domain.Badge.FIRST_MEETING;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final MemberRepository memberRepository;
    private final InitializationService initializationService;
    private final MissionService missionService;

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
}
