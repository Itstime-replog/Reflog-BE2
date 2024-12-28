package itstime.reflog.mypage.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.mypage.dto.MyPageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final MemberRepository memberRepository;

    @Transactional
    public MyPageDto.MyPageInfoResponse getMyInformation(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        return new MyPageDto.MyPageInfoResponse(
                member.getName()
        );
    }

//    @Transactional
//    public MyPageDto.MyPageMissionResponse getMission(Long memberId) {
//
//    }
}
