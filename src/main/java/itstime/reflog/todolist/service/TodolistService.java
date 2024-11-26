package itstime.reflog.todolist.service;

import org.springframework.stereotype.Service;

import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.dto.TodolistDTO;
import itstime.reflog.todolist.repository.TodolistRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodolistService {
	private final TodolistRepository todolistRepository;
	private final MemberRepository memberRepository;

	public void createTodolist(TodolistDTO.TodolistSaveOrUpdateRequest dto) {
		Member member = memberRepository.findById(dto.getMemberId())
			.orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

		Todolist todolist = Todolist.builder()
			.content(dto.getContent())
			.status(dto.isStatus())
			.member(member)
			.build();

		todolistRepository.save(todolist);
	}
}
