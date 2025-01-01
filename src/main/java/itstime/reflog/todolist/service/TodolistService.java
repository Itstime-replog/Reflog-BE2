package itstime.reflog.todolist.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.dto.TodolistDto;
import itstime.reflog.todolist.repository.TodolistRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodolistService {
	private final TodolistRepository todolistRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public void createTodolist(Long memberId, TodolistDto.TodolistSaveOrUpdateRequest dto) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

		Todolist todolist = Todolist.builder()
			.content(dto.getContent())
			.status(dto.isStatus())
			.member(member)
			.build();

		todolistRepository.save(todolist);
	}

	@Transactional(readOnly = true)
	public List<TodolistDto.TodolistResponse> getTodolistByMemberIdAndDate(Long memberId, LocalDate date) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

		List<Todolist> todolists = todolistRepository.findByMemberAndCreatedDate(member, date);

		return todolists.stream()
			.map(todolist -> new TodolistDto.TodolistResponse(
				todolist.getId(),
				todolist.getContent(),
				todolist.isStatus()
			))
			.toList();
	}

	@Transactional
	public void updateTodolist(Long todolistId, Long memberId, TodolistDto.TodolistSaveOrUpdateRequest request) {
		Todolist todolist = todolistRepository.findById(todolistId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));

		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

		todolist.update(request.getContent(), request.isStatus(), member);

		todolistRepository.save(todolist);
	}

	@Transactional
	public void deleteTodolist(Long todolistId) {
		Todolist todolist = todolistRepository.findById(todolistId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));

		todolistRepository.delete(todolist);
	}


}
