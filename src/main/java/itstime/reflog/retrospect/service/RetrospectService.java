package itstime.reflog.retrospect.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.retrospect.domain.Bad;
import itstime.reflog.retrospect.domain.Good;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.dto.RetrospectDto;
import itstime.reflog.retrospect.repository.BadRepository;
import itstime.reflog.retrospect.repository.GoodRepository;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetrospectService {
	private final MemberRepository memberRepository;
	private final RetrospectRepository retrospectRepository;
	private final GoodRepository goodRepository;
	private final BadRepository badRepository;

	@Transactional
	public void createRetrospect(Long memberId, RetrospectDto.RetrospectSaveRequest dto) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

		Retrospect retrospect = Retrospect.builder()
			.title(dto.getTitle())
			.createdDate(dto.getCreatedDate())
			.progressLevel(dto.getProgressLevel())
			.understandingLevel(dto.getUnderstandingLevel())
			.actionPlan(dto.getActionPlan())
			.visibility(dto.isVisibility())
			.studyType(dto.getStudyType())
			.member(member)
			.build();

		retrospectRepository.save(retrospect);

		List<Good> goods = dto.getGoodContents().stream()
			.map(content -> Good.builder()
				.content(content)
				.retrospect(retrospect)
				.build())
			.toList();
		goodRepository.saveAll(goods);

		List<Bad> bads = dto.getGoodContents().stream()
			.map(content -> Bad.builder()
				.content(content)
				.retrospect(retrospect)
				.build())
			.toList();
		badRepository.saveAll(bads);
	}
}
