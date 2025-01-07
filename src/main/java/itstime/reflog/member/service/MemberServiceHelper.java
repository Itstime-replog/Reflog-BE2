package itstime.reflog.member.service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberServiceHelper {

    private final MemberRepository memberRepository;

    public Member findMemberByUuid(String memberId) {
        return memberRepository.findByUuid(UUID.fromString(memberId))
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
    }
}
