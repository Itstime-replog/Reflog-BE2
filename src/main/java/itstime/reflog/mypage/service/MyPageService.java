package itstime.reflog.mypage.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.mypage.domain.MyPage;
import itstime.reflog.mypage.dto.MyPageDto;
import itstime.reflog.mypage.repository.MyPageRepository;
import itstime.reflog.schedule.domain.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MyPageRepository myPageRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MyPageDto.MyPageInfoResponse getMyInformation(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return new MyPageDto.MyPageInfoResponse(
                member.getName()
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
    }

    @Transactional
    public MyPageDto.MyPageProfileResponse getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        MyPage myPage = myPageRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MYPAGE_NOT_FOUND));

        return new MyPageDto.MyPageProfileResponse(
                myPage.getNickname(),
                myPage.getEmail()
        );
    }
}
