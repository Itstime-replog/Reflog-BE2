package itstime.reflog.analysis.service;

import itstime.reflog.analysis.dto.AnalysisDto;
import itstime.reflog.common.code.status.ErrorStatus;
import itstime.reflog.common.exception.GeneralException;
import itstime.reflog.goal.domain.DailyGoal;
import itstime.reflog.member.domain.Member;
import itstime.reflog.member.repository.MemberRepository;
import itstime.reflog.retrospect.domain.Bad;
import itstime.reflog.retrospect.domain.Good;
import itstime.reflog.retrospect.domain.Retrospect;
import itstime.reflog.retrospect.repository.RetrospectRepository;
import itstime.reflog.todolist.domain.Todolist;
import itstime.reflog.todolist.repository.TodolistRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalysisService {

    private final TodolistRepository todolistRepository;
    private final MemberRepository memberRepository;
    private final RetrospectRepository retrospectRepository;

    public AnalysisService(TodolistRepository todolistRepository, MemberRepository memberRepository, RetrospectRepository retrospectRepository){
        this.todolistRepository = todolistRepository;
        this.memberRepository = memberRepository;
        this.retrospectRepository = retrospectRepository;
    }

    @Transactional
    public AnalysisDto.AnalysisDtoResponse getWeeklyTodoList(Long memberId, LocalDate date){

        LocalDate today = LocalDate.now();
        if (date.isAfter(today)) {
            throw new GeneralException(ErrorStatus._ANALYSIS_NOT_FOUND);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new GeneralException(ErrorStatus._MEMBER_NOT_FOUND));

        //특정 날짜에 해당하는 주 데이터 조회
        LocalDate thisMonday = date.with(DayOfWeek.MONDAY);
        LocalDate nextMonday = thisMonday.plusDays(7);

        //투두
        List<Todolist> weeklyTodos = todolistRepository.findByMember(member).stream()
                .filter(todo ->
                        !todo.getCreatedDate().isBefore(thisMonday) &&
                                todo.getCreatedDate().isBefore(nextMonday)
                )
                .toList();

        int totalTodos = weeklyTodos.size();
        int completedTodos = (int) weeklyTodos.stream()
                .filter(Todolist::isStatus)
                .count();

        //회고
        List<Retrospect> weeklyRetrospect = retrospectRepository.findByMember(member).stream()
                .filter(retrospect ->
                        !retrospect.getCreatedDate().isBefore(thisMonday) &&
                                retrospect.getCreatedDate().isBefore(nextMonday)
                )
                .toList();
        //회고 수
        int totalRetrospect = weeklyRetrospect.size();

        //잘한점
        Map<String, Long> goodsFrequency = weeklyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getGoods().stream()
                        .map(Good::getContent)
                ) .collect(Collectors.groupingBy(good -> good, Collectors.counting())); //같은 good 카운팅

        long totalGoodsCount = goodsFrequency.values().stream()
                .mapToLong(Long::longValue)
                .sum();

        List<AnalysisDto.Good> topGoods = goodsFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue())) //상위 3개
                .limit(3)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalGoodsCount * 100);
                    return new AnalysisDto.Good(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());

        //부족한점
        Map<String, Long> badsFrequency = weeklyRetrospect.stream()
                .flatMap(retrospect -> retrospect.getBads().stream()
                        .map(bad -> bad.getContent()))
                .collect(Collectors.groupingBy(bad -> bad, Collectors.counting()));
        long totalBadsCount = badsFrequency.values().stream()
                .mapToLong(Long::longValue).sum();

        List<AnalysisDto.Bad> topBads = badsFrequency.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(3)
                .map(entry -> {
                    int percentage = (int) Math.round((double) entry.getValue() / totalBadsCount * 100);
                    return new AnalysisDto.Bad(entry.getKey(), percentage);
                })
                .collect(Collectors.toList());


        return new AnalysisDto.AnalysisDtoResponse(totalTodos, completedTodos, totalRetrospect, topGoods, topBads,);
    }


}
