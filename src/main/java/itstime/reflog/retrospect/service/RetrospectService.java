package itstime.reflog.retrospect.service;

import java.util.List;

import itstime.reflog.member.service.MemberServiceHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
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
	private final StudyTypeRepository studyTypeRepository;
	private final RetrospectRepository retrospectRepository;
	private final GoodRepository goodRepository;
	private final BadRepository badRepository;
	private final MemberServiceHelper memberServiceHelper;


	@Transactional
	public void createRetrospect(String memberId, RetrospectDto.RetrospectSaveOrUpdateRequest dto) {
		Member member = memberServiceHelper.findMemberByUuid(memberId);

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
	public List<RetrospectDto.RetrospectCategoryResponse> getRetrospect(String category, String memberId) {
		Member member = memberServiceHelper.findMemberByUuid(memberId);

		// Retrospect 조회
		List<Retrospect> retrospects;

		if ("기타".equals(category)) {
			// "기타"인 경우 type이 "기타:"로 시작하는 데이터 조회
			String typePrefix = "기타:%";
			retrospects = retrospectRepository.findRetrospectsByTypePrefixAndMember(typePrefix, member);
		} else {
			// 일반적인 category 조회
			retrospects = retrospectRepository.findRetrospectsByTypeAndMember(category, member);
		}

		return RetrospectDto.RetrospectCategoryResponse.fromEntity(retrospects);
	}
}