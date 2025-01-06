package itstime.reflog.member.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {
	private final MemberRepository memberRepository;

	public void deleteMember(UUID userId) {
		// 1. 데이터베이스에서 회원 정보 삭제
		Member member = memberRepository.findByUuid(userId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));
		memberRepository.delete(member);
	}

}
