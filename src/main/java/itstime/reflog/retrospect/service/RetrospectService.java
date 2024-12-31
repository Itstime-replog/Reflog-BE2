package itstime.reflog.retrospect.service;

import java.util.List;

import itstime.reflog.todolist.dto.TodolistDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.retrospect.domain.Bad;
import itstime.reflog.retrospect.domain.Good;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.domain.StudyType;
import itstime.reflog.retrospect.dto.RetrospectDto;
import itstime.reflog.retrospect.repository.BadRepository;
import itstime.reflog.retrospect.repository.GoodRepository;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import itstime.reflog.retrospect.repository.StudyTypeRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RetrospectService {
	private final MemberRepository memberRepository;
	private final StudyTypeRepository studyTypeRepository;
	private final RetrospectRepository retrospectRepository;
	private final GoodRepository goodRepository;
	private final BadRepository badRepository;

	@Transactional
	public void createRetrospect(Long memberId, RetrospectDto.RetrospectSaveOrUpdateRequest dto) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

		Retrospect retrospect = Retrospect.builder()
			.title(dto.getTitle())
			.createdDate(dto.getCreatedDate())
			.progressLevel(dto.getProgressLevel())
			.understandingLevel(dto.getUnderstandingLevel())
			.actionPlan(dto.getActionPlan())
			.visibility(dto.isVisibility())
			.member(member)
			.build();

		retrospectRepository.save(retrospect);

		List<StudyType> studyTypes = dto.getStudyTypes().stream()
			.map(type -> StudyType.builder()
				.type(type)
				.retrospect(retrospect)
				.build())
			.toList();
		studyTypeRepository.saveAll(studyTypes);

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

	@Transactional(readOnly = true)
	public RetrospectDto.RetrospectResponse getRetrospect(Long retrospectId) {
		Retrospect retrospect = retrospectRepository.findById(retrospectId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._RETROSPECT_NOT_FOUND));

		return RetrospectDto.RetrospectResponse.fromEntity(retrospect);
	}

	@Transactional
	public void updateRetrospect(Long retrospectId, RetrospectDto.RetrospectSaveOrUpdateRequest dto) {
		Retrospect retrospect = retrospectRepository.findById(retrospectId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._RETROSPECT_NOT_FOUND));

		retrospect.setTitle(dto.getTitle());
		retrospect.setCreatedDate(dto.getCreatedDate());
		retrospect.setProgressLevel(dto.getProgressLevel());
		retrospect.setUnderstandingLevel(dto.getUnderstandingLevel());
		retrospect.setActionPlan(dto.getActionPlan());
		retrospect.setVisibility(dto.isVisibility());

		List<StudyType> newStudyTypes = dto.getStudyTypes().stream()
			.map(type -> StudyType.builder()
				.type(type)
				.retrospect(retrospect)
				.build())
			.toList();
		retrospect.updateStudyTypes(newStudyTypes);

		List<Good> newGoods = dto.getGoodContents().stream()
			.map(content -> Good.builder()
				.content(content)
				.retrospect(retrospect)
				.build())
			.toList();
		retrospect.updateGoods(newGoods);

		List<Bad> newBads = dto.getBadContents().stream()
			.map(content -> Bad.builder()
				.content(content)
				.retrospect(retrospect)
				.build())
			.toList();
		retrospect.updateBads(newBads);

	}

	@Transactional(readOnly = true)
	public List<RetrospectDto.RetrospectCategoryResponse> getRetrospect(String category, Long memberId) {

		Member member = memberRepository.findById(memberId)
				.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));


		List<Retrospect> retrospects = retrospectRepository.findRetrospectsByTypeAndMember(category, member);

		return RetrospectDto.RetrospectCategoryResponse.fromEntity(retrospects);
	}
}
