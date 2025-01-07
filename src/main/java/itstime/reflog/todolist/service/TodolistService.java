package itstime.reflog.todolist.service;

import java.time.LocalDate;
import java.util.List;

import itstime.reflog.member.service.MemberServiceHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.dto.TodolistDto;
import itstime.reflog.todolist.repository.TodolistRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodolistService {
	private final TodolistRepository todolistRepository;
	private final MemberServiceHelper memberServiceHelper;


	@Transactional
	public void createTodolist(String memberId, TodolistDto.TodolistSaveOrUpdateRequest dto) {
		Member member = memberServiceHelper.findMemberByUuid(memberId);

		Todolist todolist = Todolist.builder()
			.content(dto.getContent())
			.status(dto.isStatus())
			.member(member)
			.build();

		todolistRepository.save(todolist);
	}

	@Transactional(readOnly = true)
	public List<TodolistDto.TodolistResponse> getTodolistByMemberIdAndDate(String memberId, LocalDate date) {
		Member member = memberServiceHelper.findMemberByUuid(memberId);

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
	public void updateTodolist(Long todolistId, String memberId, TodolistDto.TodolistSaveOrUpdateRequest request) {
		Todolist todolist = todolistRepository.findById(todolistId)
			.orElseThrow(() -> new GeneralException(ErrorStatus._TODO_NOT_FOUND));

		Member member = memberServiceHelper.findMemberByUuid(memberId);

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
