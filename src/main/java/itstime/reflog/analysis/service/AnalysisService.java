package itstime.reflog.analysis.service;

import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.repository.TodolistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnalysisService {

    private final TodolistRepository todolistRepository;
    private final MemberRepository memberRepository;

    public AnalysisService(TodolistRepository todolistRepository, MemberRepository memberRepository){
        this.todolistRepository = todolistRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public AnalysisDto.AnalysisDtoResponse getWeeklyTodoList(Long memberId, LocalDate date){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        LocalDate thisMonday = date.with(DayOfWeek.MONDAY);
        LocalDate nextMonday = thisMonday.plusDays(7);

        List<Todolist> weeklyTodos = todolistRepository.findByMemberAndCreatedDate(member, date).stream()
                .filter(todo ->
                        !todo.getCreatedDate().isBefore(thisMonday) &&
                                todo.getCreatedDate().isBefore(nextMonday)
                )
                .toList();

        int totalTodos = weeklyTodos.size();

        int completedTodos = (int) weeklyTodos.stream()
                .filter(Todolist::isStatus)
                .count();

        return new AnalysisDto.AnalysisDtoResponse(totalTodos, completedTodos);
    }

}
