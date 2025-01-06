package itstime.reflog.analysis.service;

import itstime.reflog.member.domain.Member;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.repository.TodolistRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class PeriodFilter {

    private final TodolistRepository todolistRepository;
    private final RetrospectRepository retrospectRepository;

    public List<Todolist> calculateTodo(LocalDate start, LocalDate end, Member member){
        return todolistRepository.findByMember(member).stream()
                .filter(todo ->
                        !todo.getCreatedDate().isBefore(start) &&
                                todo.getCreatedDate().isBefore(end)
                )
                .toList();
    }

    public List<Retrospect> calculateRetrospect(LocalDate start, LocalDate end, Member member){
        return retrospectRepository.findByMember(member).stream()
                .filter(retrospect ->
                        !retrospect.getCreatedDate().isBefore(start) &&
                                retrospect.getCreatedDate().isBefore(end)
                )
                .toList();
    }
}
